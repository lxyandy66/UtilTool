package tool.graph.plot;

final public class XLog {
	public static void log(String log) {
		System.out.println(log);
	}
	
	public static void log(String format, Object ...args) {
		log(String.format(format, args));
	}
}
