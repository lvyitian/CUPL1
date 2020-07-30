package top.dsbbs2.cupl1;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Config {
   public Map<String,Map<Integer,String>> data=new ConcurrentHashMap<>();
   public Map<String,InvType> type=new ConcurrentHashMap<>();
   public static enum InvType{
	   TWO(18,true),
	   THREE(27,true),
	   SMALL(27,false),
	   BIG(54,false);
	   public final int size;
	   public final boolean incomplete;
	   private InvType(int size,boolean incomplete)
	   {
		   this.size=size;
		   this.incomplete=incomplete;
	   }
   }
}
