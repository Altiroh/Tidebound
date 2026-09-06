package dev.tidebound.core.content;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import dev.tidebound.core.fishing.CatchProfile;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.slf4j.Logger;

/**
 * Loads Tidebound sandbox objectives from datapack JSON and atomically swaps the active catalog.
 */
public final class TideboundContentManager implements ResourceManagerReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String MILESTONE_DIRECTORY = "tidebound/milestones";
    private static final String CONTRACT_DIRECTORY = "tidebound/contracts";
    private static final String CATCH_PROFILE_DIRECTORY = "tidebound/catch_profiles";
    private static final TideboundContentManager INSTANCE = new TideboundContentManager();

    private volatile Map<String, MilestoneDefinition> milestones = Map.of();
    private volatile Map<String, ContractDefinition> contracts = Map.of();
    private volatile Map<String, CatchProfile> catchProfiles = Map.of();

    private TideboundContentManager() {
    }

    public static void register(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }

    public static Optional<MilestoneDefinition> milestone(String id) {
        return Optional.ofNullable(INSTANCE.milestones.get(normalizeLookupId(id)));
    }

    public static Optional<ContractDefinition> contract(String id) {
        return Optional.ofNullable(INSTANCE.contracts.get(normalizeLookupId(id)));
    }

    public static Optional<CatchProfile> catchProfile(String speciesId) {
        return Optional.ofNullable(INSTANCE.catchProfiles.get(normalizeLookupId(speciesId)));
    }

    public static Map<String, CatchProfile> catchProfiles() {
        return INSTANCE.catchProfiles;
    }

    public static int milestoneCount() {
        return INSTANCE.milestones.size();
    }

    public static int contractCount() {
        return INSTANCE.contracts.size();
    }

    public static List<String> milestoneIds() {
        return List.copyOf(INSTANCE.milestones.keySet());
    }

    public static List<String> contractIds() {
        return List.copyOf(INSTANCE.contracts.keySet());
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        Map<String, MilestoneDefinition> loadedMilestones = loadMilestones(resourceManager);
        Map<String, ContractDefinition> loadedContracts = loadContracts(resourceManager);
        Map<String, CatchProfile> loadedCatchProfiles = loadCatchProfiles(resourceManager);
        milestones = immutableSorted(loadedMilestones);
        contracts = immutableSorted(loadedContracts);
        catchProfiles = immutableSorted(loadedCatchProfiles);
        LOGGER.info("Loaded {} Tidebound milestones, {} repeatable contracts and {} catch profiles",
                milestones.size(), contracts.size(), catchProfiles.size());
    }

    private static Map<String, MilestoneDefinition> loadMilestones(ResourceManager manager) {
        Map<String, MilestoneDefinition> loaded = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, Resource> entry : jsonResources(manager, MILESTONE_DIRECTORY).entrySet()) {
            String id = definitionId(entry.getKey(), MILESTONE_DIRECTORY);
            JsonObject json = readObject(entry.getKey(), entry.getValue());
            MilestoneDefinition definition = new MilestoneDefinition(
                    id,
                    requiredString(json, "title"),
                    optionalString(json, "trigger", "external"),
                    parseReward(requiredObject(json, "reward"))
            );
            rejectDuplicate(loaded, id, entry.getKey());
            loaded.put(id, definition);
        }
        return loaded;
    }

    private static Map<String, ContractDefinition> loadContracts(ResourceManager manager) {
        Map<String, ContractDefinition> loaded = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, Resource> entry : jsonResources(manager, CONTRACT_DIRECTORY).entrySet()) {
            String id = definitionId(entry.getKey(), CONTRACT_DIRECTORY);
            JsonObject json = readObject(entry.getKey(), entry.getValue());
            JsonObject requirement = requiredObject(json, "requirement");
            Optional<SkillRequirement> skillRequirement = Optional.empty();
            if (json.has("requires")) {
                JsonObject requiredSkill = requiredObject(json, "requires");
                skillRequirement = Optional.of(new SkillRequirement(
                        requiredString(requiredSkill, "skill"), requiredInt(requiredSkill, "level")));
            }
            ContractDefinition definition = new ContractDefinition(
                    id,
                    requiredString(json, "title"),
                    optionalLong(json, "cooldown_ticks", 24_000L),
                    new ItemAmount(requiredString(requirement, "item"), requiredInt(requirement, "count")),
                    skillRequirement,
                    parseReward(requiredObject(json, "reward"))
            );
            rejectDuplicate(loaded, id, entry.getKey());
            loaded.put(id, definition);
        }
        return loaded;
    }

    private static Map<String, CatchProfile> loadCatchProfiles(ResourceManager manager) {
        Map<String, CatchProfile> loaded = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, Resource> entry
                : jsonResources(manager, CATCH_PROFILE_DIRECTORY).entrySet()) {
            JsonObject json = readObject(entry.getKey(), entry.getValue());
            String species = normalizeLookupId(requiredString(json, "species"));
            CatchProfile profile = new CatchProfile(
                    species,
                    requiredInt(json, "min_weight_grams"),
                    requiredInt(json, "max_weight_grams"),
                    requiredInt(json, "reference_weight_grams"),
                    requiredInt(json, "base_value_tides")
            );
            rejectDuplicate(loaded, species, entry.getKey());
            loaded.put(species, profile);
        }
        return loaded;
    }

    private static RewardDefinition parseReward(JsonObject json) {
        long tides = optionalLong(json, "tides", 0L);
        Map<String, Long> skillXp = new LinkedHashMap<>();
        if (json.has("skill_xp")) {
            JsonObject xpObject = requiredObject(json, "skill_xp");
            xpObject.entrySet().forEach(entry -> skillXp.put(entry.getKey(), entry.getValue().getAsLong()));
        }

        List<ItemAmount> items = new ArrayList<>();
        if (json.has("items")) {
            JsonArray array = json.getAsJsonArray("items");
            for (JsonElement element : array) {
                if (!element.isJsonObject()) {
                    throw new JsonParseException("Reward items must be JSON objects");
                }
                JsonObject item = element.getAsJsonObject();
                items.add(new ItemAmount(requiredString(item, "item"), requiredInt(item, "count")));
            }
        }
        return new RewardDefinition(tides, skillXp, items);
    }

    private static Map<ResourceLocation, Resource> jsonResources(ResourceManager manager, String directory) {
        return manager.listResources(directory, location -> location.getPath().endsWith(".json"));
    }

    private static JsonObject readObject(ResourceLocation location, Resource resource) {
        try (Reader reader = resource.openAsReader()) {
            JsonElement parsed = JsonParser.parseReader(reader);
            if (!parsed.isJsonObject()) {
                throw new JsonParseException("Root must be a JSON object");
            }
            return parsed.getAsJsonObject();
        } catch (IOException | RuntimeException exception) {
            throw new JsonParseException("Invalid Tidebound definition " + location + ": "
                    + exception.getMessage(), exception);
        }
    }

    private static String definitionId(ResourceLocation location, String directory) {
        String path = location.getPath();
        String prefix = directory + "/";
        if (!path.startsWith(prefix) || !path.endsWith(".json")) {
            throw new JsonParseException("Unexpected definition path: " + location);
        }
        String relative = path.substring(prefix.length(), path.length() - ".json".length());
        return location.getNamespace() + ":" + relative;
    }

    private static String requiredString(JsonObject object, String field) {
        if (!object.has(field) || !object.get(field).isJsonPrimitive()) {
            throw new JsonParseException("Missing string field: " + field);
        }
        return object.get(field).getAsString();
    }

    private static String optionalString(JsonObject object, String field, String fallback) {
        return object.has(field) ? requiredString(object, field) : fallback;
    }

    private static int requiredInt(JsonObject object, String field) {
        if (!object.has(field) || !object.get(field).isJsonPrimitive()) {
            throw new JsonParseException("Missing integer field: " + field);
        }
        return object.get(field).getAsInt();
    }

    private static long optionalLong(JsonObject object, String field, long fallback) {
        if (!object.has(field)) {
            return fallback;
        }
        if (!object.get(field).isJsonPrimitive()) {
            throw new JsonParseException("Invalid numeric field: " + field);
        }
        return object.get(field).getAsLong();
    }

    private static JsonObject requiredObject(JsonObject object, String field) {
        if (!object.has(field) || !object.get(field).isJsonObject()) {
            throw new JsonParseException("Missing object field: " + field);
        }
        return object.getAsJsonObject(field);
    }

    private static <T> void rejectDuplicate(Map<String, T> values, String id, ResourceLocation source) {
        if (values.containsKey(id)) {
            throw new JsonParseException("Duplicate Tidebound id " + id + " at " + source);
        }
    }

    private static <T> Map<String, T> immutableSorted(Map<String, T> values) {
        List<String> keys = new ArrayList<>(values.keySet());
        Collections.sort(keys);
        Map<String, T> sorted = new LinkedHashMap<>();
        keys.forEach(key -> sorted.put(key, values.get(key)));
        return Collections.unmodifiableMap(sorted);
    }

    private static String normalizeLookupId(String value) {
        return value.strip().toLowerCase(java.util.Locale.ROOT);
    }
}
