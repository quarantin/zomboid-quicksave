package zombie.quicksave;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import se.krka.kahlua.integration.annotations.LuaMethod;

import zombie.javamods.Filesystem;
import zombie.javamods.Log;
import zombie.javamods.mod.JavaMod;


public class QuickSave extends JavaMod {

	public final static String modId = "QuickSave";

	@Override
	public List<Class<?>> getClassesToExpose() {
		return null;
	}

	@Override
	public List<Object> getObjectsWithGlobalFunctions() {
		return Arrays.asList(this);
	}

	@Override
	public void startup() {
	}

	@LuaMethod(name = "QuickSave", global = true)
	public void QuickSave(String saveDir) {
		newSaveThread(saveDir, true);
	}

	@LuaMethod(name = "QuickLoad", global = true)
	public void QuickLoad(String saveDir) {
		newSaveThread(saveDir, false);
	}

	private void newSaveThread(String saveDir, boolean save) {
		new Thread(new Runnable() {
			public void run() {
				QuickSaveLoad(saveDir, save);
			}
		}).start();
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
			Log.warn(modId + ": I/O error occured while copying file: " + inputFile);
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

	public static void QuickSaveLoad(String savePath, boolean save) {

		File inputDir, outputDir;
		Log.info(modId + ": Saving game " + savePath);

		if (savePath == null || savePath.equals("")) {
			Log.warn(modId + ": savePath is missing");
			return;
		}

		if (savePath.contains("..")) {
			Log.warn(modId + ": relative paths not allowed");
			return;
		}

		savePath = savePath.replace("/",  File.separator);
		savePath = savePath.replace("\\", File.separator);

		File saveDir   = Filesystem.getUserProfileSaveDir();
		File backupDir = Filesystem.getUserProfileSubdir(modId + "s" );

		if (save) {
			inputDir  = new File(saveDir,   savePath);
			outputDir = new File(backupDir, savePath);
		}
		else {
			inputDir  = new File(backupDir, savePath);
			outputDir = new File(saveDir,   savePath);
		}

		if (!inputDir.exists()) {
			Log.warn(modId + ": Path does not exists: " + inputDir.getAbsolutePath());
			return;
		}

		if (outputDir.exists())
			deleteRecursively(outputDir);

		copyRecursively(inputDir, outputDir);
	}
}
