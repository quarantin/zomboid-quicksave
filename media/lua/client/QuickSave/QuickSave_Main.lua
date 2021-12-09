local module = {}

module.modId = "QuickSave"

module.playerIsDead = false

module.error = function()
	print("ERROR: " .. module.modId .. ": " ..
		"You have to install the missing files! " ..
		"For more information, please visit: " ..
		"https://github.com/quarantin/zomboid-quicksave#readme"
	)
end

module.QuickSave = function()

	if QuickSave and not module.playerIsDead then
		return QuickSave(module.getLastSave())
	end

	module.error()
end

module.QuickLoad = function()

	if QuickLoad then
		return QuickLoad(module.getLastSave())
	end

	module.error()
end

module.getLastSave = function()
	local lastSave = getLatestSave()
	return lastSave[2] .. getFileSeparator() .. lastSave[1]
end

module.OnPlayerDeath = function()
	module.playerIsDead = true
end

module.OnPostSave = function()

	if module.playerIsDead then
		module.QuickLoad()
	else
		module.QuickSave()
	end
end

module.OnKeyPressed = function(key)

	if key == Keyboard.KEY_F9 then
		saveGame()
		module.QuickSave()
	end
end

Events.OnPlayerDeath.Add(module.OnPlayerDeath)
Events.OnPostSave.Add(module.OnPostSave)
Events.OnKeyPressed.Add(module.OnKeyPressed)
