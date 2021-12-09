package zombie.Lua;

import java.util.HashSet;
import java.util.Iterator;

import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.integration.expose.LuaJavaClassExposer;
import se.krka.kahlua.j2se.J2SEPlatform;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.Platform;

import zombie.Lua.QuickSave;


public class CustomLuaManager {

	public static KahluaTable env;
	public static J2SEPlatform platform;
	public static KahluaConverterManager converterManager;
	public static CustomLuaManager.Exposer exposer;

	public static void init(KahluaConverterManager converterManager, J2SEPlatform platform, KahluaTable env) {
		System.out.println("QuickSave: CustomLuaManager.init()");
		CustomLuaManager.platform = new J2SEPlatform();
		CustomLuaManager.env = platform.newEnvironment();
		CustomLuaManager.converterManager = new KahluaConverterManager();
		exposer = new CustomLuaManager.Exposer(converterManager, platform, env);
		exposer.exposeAll();	
		System.out.println("QuickSave: Done!");
	}

	private static class Exposer extends LuaJavaClassExposer {

		private HashSet<Class> exposed = new HashSet<>();

		public Exposer(KahluaConverterManager converterManager, Platform platform, KahluaTable table) {
			super(converterManager, platform, table);
		}

		public void exposeAll() {
			setExposed(QuickSave.class);

			Iterator<Class> iterator = exposed.iterator();
			while (iterator.hasNext())
				exposeLikeJavaRecursively(iterator.next(), env);

			exposeGlobalFunctions(new CustomLuaManager.GlobalObjects());
		}

		public void setExposed(Class classs) {
			exposed.add(classs);
		}

		public boolean shouldExpose(Class classs) {
			return classs != null && exposed.contains(classs);
		}
	}

	private static class GlobalObjects {

		@LuaMethod(name = "QuickSave", global = true)
		public static void QuickSave(String savePath) {
			QuickSave.QuickSaveLoad(savePath, true);
		}

		@LuaMethod(name = "QuickLoad", global = true)
		public static void QuickLoad(String savePath) {
			QuickSave.QuickSaveLoad(savePath, false);
		}
	}
}
