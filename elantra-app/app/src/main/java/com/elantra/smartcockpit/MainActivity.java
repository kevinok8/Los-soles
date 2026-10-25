package com.elantra.smartcockpit;

import android.app.*;
import android.os.*;
import android.provider.Settings;
import android.content.*;
import android.content.pm.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
  private final int BG=Color.rgb(5,10,18), CARD=Color.rgb(9,20,34), BLUE=Color.rgb(110,187,255), MUTED=Color.rgb(140,165,190);
  private LinearLayout root;
  private TextView clock;

  @Override public void onCreate(Bundle b){
    super.onCreate(b);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    build();
    tick();
  }

  private TextView tv(String s,int sp,int color,boolean bold){
    TextView t=new TextView(this); t.setText(s); t.setTextSize(sp); t.setTextColor(color);
    t.setGravity(Gravity.CENTER_VERTICAL); t.setPadding(16,8,16,8);
    if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
    return t;
  }

  private void build(){
    root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(BG); root.setPadding(18,14,18,12);

    LinearLayout top=new LinearLayout(this); top.setGravity(Gravity.CENTER_VERTICAL);
    TextView title=tv("HYUNDAI  •  ELANTRA 2016  •  1.8L",20,Color.WHITE,true);
    clock=tv("",18,BLUE,true); clock.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
    top.addView(title,new LinearLayout.LayoutParams(0,58,1));
    top.addView(clock,new LinearLayout.LayoutParams(260,58));
    root.addView(top);

    LinearLayout middle=new LinearLayout(this); middle.setOrientation(LinearLayout.HORIZONTAL);
    middle.addView(card("NAVEGACIÓN","Maps / Waze","maps"),new LinearLayout.LayoutParams(0,210,1.45f));
    middle.addView(card("MÚSICA","Spotify / Música","music"),new LinearLayout.LayoutParams(0,210,1.15f));
    middle.addView(vehicleCard(),new LinearLayout.LayoutParams(0,210,1));
    root.addView(middle);

    GridLayout grid=new GridLayout(this); grid.setColumnCount(6); grid.setPadding(0,12,0,8);
    addTile(grid,"MAPS","maps"); addTile(grid,"SPOTIFY","music"); addTile(grid,"CARPLAY","carplay");
    addTile(grid,"VEHÍCULO","vehicle"); addTile(grid,"CÁMARA","camera"); addTile(grid,"AJUSTES","settings");
    root.addView(grid,new LinearLayout.LayoutParams(-1,115));

    LinearLayout bottom=new LinearLayout(this);
    addBottom(bottom,"RADIO","radio"); addBottom(bottom,"DVR","dvr"); addBottom(bottom,"CAMBIAR HOME","home_settings");
    root.addView(bottom,new LinearLayout.LayoutParams(-1,62));

    setContentView(root);
  }

  private View card(String head,String sub,String slot){
    LinearLayout c=new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setBackgroundColor(CARD); c.setPadding(10,10,10,10);
    c.addView(tv(head,16,BLUE,true),new LinearLayout.LayoutParams(-1,52));
    TextView s=tv(sub,20,Color.WHITE,true); s.setGravity(Gravity.CENTER); c.addView(s,new LinearLayout.LayoutParams(-1,0,1));
    TextView hint=tv("Toca para abrir • Mantén para asignar app",11,MUTED,false); hint.setGravity(Gravity.CENTER); c.addView(hint,new LinearLayout.LayoutParams(-1,38));
    c.setOnClickListener(v->launch(slot)); c.setOnLongClickListener(v->{choose(slot);return true;});
    LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-1); lp.setMargins(6,0,6,0); c.setLayoutParams(lp);
    return c;
  }

  private View vehicleCard(){
    LinearLayout c=new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setBackgroundColor(CARD); c.setPadding(14,10,14,10);
    c.addView(tv("VEHÍCULO",16,BLUE,true));
    c.addView(tv("TEMP. MOTOR     -- °C",16,Color.WHITE,true));
    c.addView(tv("BATERÍA         --.- V",16,Color.WHITE,true));
    c.addView(tv("RPM             ----",16,Color.WHITE,true));
    c.addView(tv("OBD2 pendiente",11,MUTED,false));
    c.setOnClickListener(v->launch("vehicle"));
    return c;
  }

  private void addTile(GridLayout g,String label,String slot){
    TextView t=tv(label,14,Color.WHITE,true); t.setGravity(Gravity.CENTER); t.setBackgroundColor(CARD);
    GridLayout.LayoutParams p=new GridLayout.LayoutParams(); p.width=0; p.height=100; p.columnSpec=GridLayout.spec(GridLayout.UNDEFINED,1f); p.setMargins(5,5,5,5);
    t.setLayoutParams(p); t.setOnClickListener(v->launch(slot));
    if(!slot.equals("vehicle")&&!slot.equals("settings"))t.setOnLongClickListener(v->{choose(slot);return true;});
    g.addView(t);
  }

  private void addBottom(LinearLayout l,String label,String slot){
    TextView t=tv(label,12,BLUE,true); t.setGravity(Gravity.CENTER); t.setBackgroundColor(Color.rgb(7,16,28));
    LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-1,1); p.setMargins(5,0,5,0); l.addView(t,p);
    t.setOnClickListener(v->launch(slot)); if(slot.equals("radio")||slot.equals("dvr"))t.setOnLongClickListener(v->{choose(slot);return true;});
  }

  private void tick(){
    final Handler h=new Handler();
    h.post(new Runnable(){public void run(){ if(clock!=null)clock.setText(new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date())); h.postDelayed(this,1000); }});
  }

  private String pretty(String s){ if(s.equals("music"))return "Música"; if(s.equals("camera"))return "Cámara"; return s.toUpperCase(Locale.ROOT); }

  private void choose(final String slot){
    PackageManager pm=getPackageManager();
    Intent q=new Intent(Intent.ACTION_MAIN); q.addCategory(Intent.CATEGORY_LAUNCHER);
    List<ResolveInfo> all=pm.queryIntentActivities(q,0); final ArrayList<ResolveInfo> apps=new ArrayList<>();
    for(ResolveInfo r:all) if(r.activityInfo!=null&&!getPackageName().equals(r.activityInfo.packageName)) apps.add(r);
    Collections.sort(apps,(a,b)->a.loadLabel(pm).toString().compareToIgnoreCase(b.loadLabel(pm).toString()));
    String[] names=new String[apps.size()]; for(int i=0;i<apps.size();i++)names[i]=apps.get(i).loadLabel(pm).toString();
    new AlertDialog.Builder(this).setTitle("Asignar "+pretty(slot)).setItems(names,(d,w)->{
      ResolveInfo r=apps.get(w); String c=r.activityInfo.packageName+"/"+r.activityInfo.name;
      getPreferences(MODE_PRIVATE).edit().putString("slot_"+slot,c).apply();
      Toast.makeText(this,"Asignado a "+names[w],Toast.LENGTH_SHORT).show();
    }).setNegativeButton("Cancelar",null).show();
  }

  private void launch(String slot){
    if(slot.equals("settings")){startActivity(new Intent(Settings.ACTION_SETTINGS));return;}
    if(slot.equals("home_settings")){
      try{startActivity(new Intent(Settings.ACTION_HOME_SETTINGS));}catch(Exception e){startActivity(new Intent(Settings.ACTION_SETTINGS));} return;
    }
    if(slot.equals("vehicle")){
      new AlertDialog.Builder(this).setTitle("Vehículo • OBD2").setMessage("Temperatura: -- °C\nRPM: ----\nVoltaje: --.- V\n\nLos datos reales se activarán cuando integremos OBD2.").setPositiveButton("OK",null).show(); return;
    }
    String saved=getPreferences(MODE_PRIVATE).getString("slot_"+slot,null);
    if(saved!=null){try{ComponentName cn=ComponentName.unflattenFromString(saved); Intent i=new Intent(Intent.ACTION_MAIN);i.addCategory(Intent.CATEGORY_LAUNCHER);i.setComponent(cn);startActivity(i);return;}catch(Exception e){}}
    String[] pkgs=slot.equals("maps")?new String[]{"com.google.android.apps.maps","com.waze"}:
      slot.equals("music")?new String[]{"com.spotify.music","com.google.android.apps.youtube.music"}:
      slot.equals("dvr")?new String[]{"com.vidure.veecar"}:new String[]{};
    for(String p:pkgs){Intent i=getPackageManager().getLaunchIntentForPackage(p); if(i!=null){startActivity(i);return;}}
    choose(slot);
  }
}
