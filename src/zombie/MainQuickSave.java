package zombie;

import java.io.IOException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.j2se.J2SEPlatform;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.Platform;

import zombie.gameStates.MainScreenState;
import zombie.Lua.CustomLuaManager;
import zombie.Lua.LuaManager;

public class MainQuickSave implements Runnable {

	private MainQuickSave() {
		new Thread(new Runnable(this)).start();
	}

	private boolean isLuaManagerReady() {
		Field envField = LuaManager.class.getDeclaredField("env");
		KahluaTable env = (KahluaTable)envField.get(null);
		return env.get("Calendar") != null;
	}

	private void run() {//thread() throws IllegalAccessException, NoSuchFieldException {

		while (!isLuaManagerReady()) {

			System.out.println("LuaManager not ready yet, sleeping 1 second...");

			try {

				Thread.sleep(1);

			} catch (Exception interrupted) {}
		}

		Field platformField = LuaManager.class.getDeclaredField("platform");
		J2SEPlatform platform = (J2SEPlatform)platformField.get(null);

		Field converterManagerField = LuaManager.class.getDeclaredField("converterManager");
		KahluaConverterManager converterManager = (KahluaConverterManager)converterManagerField.get(null);

		Field envField = LuaManager.class.getDeclaredField("env");
		KahluaTable env = (KahluaTable)envField.get(null);

		CustomLuaManager.init(converterManager, platform, env);
	}

	private static void init() {

		try {
			new MainQuickSave();
		}
		catch (IllegalAccessException|NoSuchFieldException error) {
			error.printStackTrace();
		}
	}

	private static void startZomboid(String[] args) {

		try {
			final Object[] arg = new Object[]{ args };
			MainScreenState.class.getDeclaredMethod("main", String[].class).invoke(null, arg);
		}
		catch (IOException error) {
			error.printStackTrace();
		}
	}

	public static void main(String[] args) {
		init();
		startZomboid(args);
	}
}
