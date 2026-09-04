// Place this file in kubejs/server_scripts/ when KubeJS is part of the modpack.
// The Java mod remains the source of truth; this adapter only invokes its permission-2 commands.

const TideboundToken = /^[a-zA-Z0-9_.:/-]+$/

function tideboundToken(value, label) {
  const token = String(value)
  if (!TideboundToken.test(token)) {
    throw new Error(`Invalid Tidebound ${label}: ${token}`)
  }
  return token
}

global.Tidebound = Object.freeze({
  milestone(server, playerName, milestoneId) {
    const player = tideboundToken(playerName, 'player')
    const id = tideboundToken(milestoneId, 'milestone')
    return server.runCommandSilent(`tidebound progression milestone complete ${player} ${id}`)
  },

  rewardOnce(server, playerName, receiptId, tides) {
    const player = tideboundToken(playerName, 'player')
    const receipt = tideboundToken(receiptId, 'receipt')
    const amount = Math.max(1, Math.floor(Number(tides)))
    return server.runCommandSilent(`tidebound progression reward-once ${player} ${receipt} ${amount}`)
  },

  skillXp(server, playerName, skillId, xp) {
    const player = tideboundToken(playerName, 'player')
    const skill = tideboundToken(skillId, 'skill')
    const amount = Math.max(1, Math.floor(Number(xp)))
    return server.runCommandSilent(`tidebound progression skill grant ${player} ${skill} ${amount}`)
  }
})
