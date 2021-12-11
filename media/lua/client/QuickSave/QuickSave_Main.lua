local module = {}

module.playerIsDead = false

module.error = function()
	print("ERROR: QuickSave: There seems to be a problem with the JavaMods loader. " ..
		"Check here to get help: https://github.com/quarantin/javamods#readme")
end

module.getLastSave = function()
	local lastSave = getLatestSave()
	return lastSave[2] .. getFileSeparator() .. lastSave[1]
end

module.QuickSave = function()

	if not QuickSave then
		return module.error()
	end

	if not module.playerIsDead then
		return QuickSave(module.getLastSave())
	end
end

module.QuickLoad = function()

	if QuickLoad then
		return QuickLoad(module.getLastSave())
	end

	module.error()
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
