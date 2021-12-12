package zombie.quicksave;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import se.krka.kahlua.integration.annotations.LuaMethod;

import zombie.debug.DebugLog;
import zombie.ZomboidFileSystem;
import zombie.javamods.JavaMod;


public class QuickSave extends JavaMod {

	public final static String modId = "QuickSave";

	@Override
	public List<Class<?>> getExposedClasses() {
		return null;
	}

	@Override
	public List<Object> getGlobalObjects() {
		return Arrays.asList(this);
	}

	@Override
	public void startup() {
		DebugLog.Lua.println("QUICKSAVE STARTUP!");
	}

	@LuaMethod(name = "QuickSave", global = true)
	public void QuickSave(String saveDir) {
		QuickSaveLoad(saveDir, true);
	}

	@LuaMethod(name = "QuickLoad", global = true)
	public void QuickLoad(String saveDir) {
		QuickSaveLoad(saveDir, false);
	}

	private static String getSavePath() {
		return ZomboidFileSystem.instance.getSaveDir();
	}

	private static String getBackupPath() {

		String backupPath = ZomboidFileSystem.instance.getCacheDirSub("QuickSaves");
		File backupDir = new File(backupPath);

		if (!backupDir.exists())
			backupDir.mkdirs();

		return backupPath;
	}

	private static void deleteRecursively(File root) {

		if (root.isDirectory())
			for (String filename : root.list())
				deleteRecursively(new File(root, filename));

		else
			root.delete();
	}

	private static void copyFile(File inputFile, File outputFile) {

		File parent = outputFile.getParentFile();
		if (!parent.exists())
			parent.mkdirs();

		int bufSize = (int)inputFile.length();
		byte[] buffer = new byte[bufSize];

		try {
			FileInputStream input = new FileInputStream(inputFile);
			FileOutputStream output = new FileOutputStream(outputFile);
		
			input.read(buffer);
			output.write(buffer);

			input.close();
			output.close();
		}
		catch (IOException error) {
			DebugLog.Lua.warn(modId + ": I/O error occured while copying file: " + inputFile);
		}
	}

	private static void copyRecursively(File inputDir, File outputDir) {

		for (String filename : inputDir.list()) {

			File inputFile = new File(inputDir, filename);
			File outputFile = new File(outputDir, filename);

			if (inputFile.isFile())
				copyFile(inputFile, outputFile);

			else if (inputFile.isDirectory())
				copyRecursively(inputFile, outputFile);
		}
	}

	public static void QuickSaveLoad(String saveDir, boolean save) {

		String inputPath;
		String outputPath;

		DebugLog.Lua.println("QUICKSAVE: SAVING GAME " + saveDir);

		if (saveDir == null || saveDir.equals("")) {
			DebugLog.Lua.warn(modId + ": saveDir is missing");
			return;
		}

		if (saveDir.contains("..")) {
			DebugLog.Lua.warn(modId + ": relative paths not allowed");
			return;
		}

		saveDir = saveDir.replace("/", File.separator);
		saveDir = saveDir.replace("\\", File.separator);

		if (save) {
			inputPath = getSavePath();
			outputPath = getBackupPath();
		}
		else {
			inputPath = getBackupPath();
			outputPath = getSavePath();
		}

		File inputDir = new File(inputPath, saveDir);
		File outputDir = new File(outputPath, saveDir);

		if (!inputDir.exists()) {
			DebugLog.Lua.warn(modId + ": Path does not exists: " + inputDir.getAbsolutePath());
			return;
		}

		if (outputDir.exists())
			deleteRecursively(outputDir);

		copyRecursively(inputDir, outputDir);
	}
}
