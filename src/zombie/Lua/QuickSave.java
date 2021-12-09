package zombie.Lua;

import java.util.ArrayList;
import java.util.HashMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import zombie.debug.DebugLog;
import zombie.ZomboidFileSystem;


public class QuickSave {

	private final static String MOD_ID = "QuickSave";

	private static boolean initialized;
	private static ZomboidFileSystem filesystem;

	private static void initialize() {

		if (!initialized) {
			initialized = true;
			filesystem = ZomboidFileSystem.instance;
		}
	}

	private static String getBackupPath() {
		String backupPath = filesystem.getCacheDirSub(MOD_ID);
		filesystem.ensureFolderExists(backupPath);
		return backupPath;
	}

	private static String getSavePath() {
		return filesystem.getSaveDir();
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
			DebugLog.Lua.warn("QuickSave: I/O error occured while copying file: " + inputFile);
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

		initialize();

		String inputPath;
		String outputPath;

		if (saveDir == null || saveDir.equals("")) {
			DebugLog.Lua.warn("QuickSave: saveDir is missing");
			return;
		}

		if (saveDir.contains("..")) {
			DebugLog.Lua.warn("QuickSave: relative paths not allowed");
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
			DebugLog.Lua.warn("QuickSave: Path does not exists: " + inputDir.getAbsolutePath());
			return;
		}

		if (outputDir.exists())
			deleteRecursively(outputDir);

		copyRecursively(inputDir, outputDir);
	}
}
