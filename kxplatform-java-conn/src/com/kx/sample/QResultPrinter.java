package com.kx.sample;

import java.io.PrintStream;
import java.util.Map;

import com.kx.q.c.Dict;
import com.kx.q.c.Flip;

public class QResultPrinter
{
	String prefix = "";
	PrintStream out;
	public QResultPrinter(PrintStream out) {
		this.out = out;
	}

	public void printResult(Object o){
		if(o instanceof Object[]) {
			printArray((Object[])o);
		} else if(o instanceof Map) {
			printMap((Map)o);
		} else if(o instanceof Dict) {
			printDict((Dict) o);
		} else if (o instanceof Flip) {
			printFlip((Flip) o);
		} else if(o instanceof  char[]){
			out.println(new String((char[])o));
		} else {
			out.println(prefix + o.toString());
		}
	}

	private void printArray(Object[] arr) {
		out.println(prefix + "[");
		String oldPrefix = prefix;
		prefix = prefix + " ";
		for(int i=0; i< arr.length; i++){
			printResult(arr[i]);
		}

		prefix = oldPrefix;
		out.println(prefix + "]");
	}

	private void printMap(Map map) {
		out.println(prefix + "{");
		String oldPrefix = prefix;
		prefix = prefix + "  ";
		for(Object key : map.keySet()) {
			out.println(oldPrefix + " " + key.toString() + ":");
			printResult(map.get(key));
		}

		prefix = oldPrefix;
		out.println(prefix + "}");
	}

	private void printFlip(Flip flip)
	{
		out.println(prefix + "(");
		String oldPrefix = prefix;
		prefix = prefix + "  ";
		for(int i=0; i< flip.y.length; i++)
		{
			out.println(oldPrefix + " " +flip.x[i]);
			printResult(flip.y);
		}

		prefix = oldPrefix;
		out.println(prefix + ")");
	}

	private void printDict(Dict dict)
	{
		out.println(prefix + "<");
		String oldPrefix = prefix;
		prefix = prefix + " ";
		printResult(dict.x);
		out.println(prefix + "->");
		printResult(dict.y);
		prefix = oldPrefix;
		out.println(prefix + ">");
	}

}
