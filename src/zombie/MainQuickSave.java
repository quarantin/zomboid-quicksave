package zombie;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.j2se.J2SEPlatform;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.Platform;

import zombie.gameStates.MainScreenState;
import zombie.Lua.CustomLuaManager;
import zombie.Lua.LuaManager;

public class MainQuickSave {

	public static void start() {
		new Thread(new Runnable() {
			public void run() {

				try {
					Thread.sleep(10 * 1000);
				}
				catch (Exception interrupted) {}

				try {
					Field platformField = LuaManager.class.getDeclaredField("platform");
					J2SEPlatform platform = (J2SEPlatform)platformField.get(null);

					Field converterManagerField = LuaManager.class.getDeclaredField("converterManager");
					KahluaConverterManager converterManager = (KahluaConverterManager)converterManagerField.get(null);

					Field envField = LuaManager.class.getDeclaredField("env");
					KahluaTable env = (KahluaTable)envField.get(null);

					CustomLuaManager.init(converterManager, platform, env);
				}
				catch (Exception error) {
					error.printStackTrace();
				}
			}
		}).start();
	}

	public static void main(String[] args) {
		start();
		try {
			final Object[] arg = new Object[]{ args };
			MainScreenState.class.getDeclaredMethod("main", String[].class).invoke(null, arg);
		}
		catch (Exception error) {
			error.printStackTrace();
		}
	}
}
