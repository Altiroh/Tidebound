package dev.tidebound.core.data;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Immutable progression ledger for one-shot receipts, milestones, repeatable contracts and skills.
 */
public record PlayerProgress(
        List<String> claimedReceipts,
        List<String> completedMilestones,
        Map<String, Long> skillXp,
        Map<String, ContractProgress> contracts
) {
    public static final int MAX_RECEIPTS = 4_096;
    public static final int MAX_MILESTONES = 1_024;
    public static final int MAX_SKILLS = 32;
    public static final int MAX_CONTRACTS = 256;
    public static final long MAX_SKILL_XP = 1_000_000_000_000L;

    public PlayerProgress {
        claimedReceipts = normalizedIds(claimedReceipts, MAX_RECEIPTS, "receipts");
        completedMilestones = normalizedIds(completedMilestones, MAX_MILESTONES, "milestones");
        skillXp = normalizedXp(skillXp);
        contracts = normalizedContracts(contracts);
    }

    public static PlayerProgress empty() {
        return new PlayerProgress(List.of(), List.of(), Map.of(), Map.of());
    }

    public boolean hasReceipt(String receiptId) {
        return claimedReceipts.contains(validId(receiptId));
    }

    public boolean hasCompletedMilestone(String milestoneId) {
        return completedMilestones.contains(validId(milestoneId));
    }

    public long skillXp(String skillId) {
        return skillXp.getOrDefault(validId(skillId), 0L);
    }

    public ContractProgress contract(String contractId) {
        return contracts.getOrDefault(validId(contractId), ContractProgress.fresh());
    }

    public PlayerProgress claimReceipt(String receiptId) {
        String id = validId(receiptId);
        if (claimedReceipts.contains(id)) {
            throw new IllegalStateException("Reward receipt already claimed: " + id);
        }
        if (claimedReceipts.size() >= MAX_RECEIPTS) {
            throw new IllegalStateException("Reward receipt limit reached");
        }
        List<String> updated = new ArrayList<>(claimedReceipts);
        updated.add(id);
        return new PlayerProgress(updated, completedMilestones, skillXp, contracts);
    }

    public PlayerProgress completeMilestone(String milestoneId) {
        String id = validId(milestoneId);
        if (completedMilestones.contains(id)) {
            throw new IllegalStateException("Milestone already completed: " + id);
        }
        if (completedMilestones.size() >= MAX_MILESTONES) {
            throw new IllegalStateException("Milestone limit reached");
        }
        List<String> updated = new ArrayList<>(completedMilestones);
        updated.add(id);
        return new PlayerProgress(claimedReceipts, updated, skillXp, contracts);
    }

    public PlayerProgress completeContract(String contractId, long gameTime, long cooldownTicks) {
        String id = validId(contractId);
        ContractProgress updatedContract = contract(id).complete(gameTime, cooldownTicks);
        if (!contracts.containsKey(id) && contracts.size() >= MAX_CONTRACTS) {
            throw new IllegalStateException("Contract ledger limit reached");
        }
        Map<String, ContractProgress> updated = new TreeMap<>(contracts);
        updated.put(id, updatedContract);
        return new PlayerProgress(claimedReceipts, completedMilestones, skillXp, updated);
    }

    public PlayerProgress addSkillXp(Map<String, Long> rewards) {
        Objects.requireNonNull(rewards, "rewards");
        Map<String, Long> updated = new TreeMap<>(skillXp);
        for (Map.Entry<String, Long> entry : rewards.entrySet()) {
            String skill = validId(entry.getKey());
            long amount = Objects.requireNonNull(entry.getValue(), "skill XP amount");
            if (amount <= 0) {
                throw new IllegalArgumentException("Skill XP reward must be positive");
            }
            long current = updated.getOrDefault(skill, 0L);
            if (amount > MAX_SKILL_XP - current) {
                throw new IllegalArgumentException("Skill XP limit exceeded for " + skill);
            }
            if (!updated.containsKey(skill) && updated.size() >= MAX_SKILLS) {
                throw new IllegalStateException("Skill limit reached");
            }
            updated.put(skill, current + amount);
        }
        return new PlayerProgress(claimedReceipts, completedMilestones, updated, contracts);
    }

    private static List<String> normalizedIds(List<String> values, int maximum, String label) {
        Objects.requireNonNull(values, label);
        LinkedHashSet<String> unique = new LinkedHashSet<>();
        for (String value : values) {
            unique.add(validId(value));
        }
        if (unique.size() > maximum) {
            throw new IllegalArgumentException("Too many " + label);
        }
        return List.copyOf(unique);
    }

    private static Map<String, Long> normalizedXp(Map<String, Long> values) {
        Objects.requireNonNull(values, "skillXp");
        if (values.size() > MAX_SKILLS) {
            throw new IllegalArgumentException("Too many skills");
        }
        Map<String, Long> normalized = new TreeMap<>();
        values.forEach((key, value) -> {
            String id = validId(key);
            long xp = Objects.requireNonNull(value, "skill XP");
            if (xp < 0 || xp > MAX_SKILL_XP) {
                throw new IllegalArgumentException("Invalid skill XP for " + id);
            }
            normalized.put(id, xp);
        });
        return Map.copyOf(normalized);
    }

    private static Map<String, ContractProgress> normalizedContracts(Map<String, ContractProgress> values) {
        Objects.requireNonNull(values, "contracts");
        if (values.size() > MAX_CONTRACTS) {
            throw new IllegalArgumentException("Too many contracts");
        }
        Map<String, ContractProgress> normalized = new TreeMap<>();
        values.forEach((key, value) -> normalized.put(validId(key), Objects.requireNonNull(value, "contract")));
        return Map.copyOf(normalized);
    }

    private static String validId(String value) {
        String id = Objects.requireNonNull(value, "id").strip().toLowerCase(java.util.Locale.ROOT);
        if (id.isBlank() || id.length() > 128 || !id.matches("[a-z0-9_.:/-]+")) {
            throw new IllegalArgumentException("Invalid Tidebound id: " + value);
        }
        return id;
    }
}
