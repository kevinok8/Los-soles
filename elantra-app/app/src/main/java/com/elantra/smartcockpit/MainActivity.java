package com.elantra.smartcockpit;

import android.app.*;
import android.os.*;
import android.provider.Settings;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
  CockpitView cockpit;

  @Override public void onCreate(Bundle b){
    super.onCreate(b);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    getWindow().getDecorView().setSystemUiVisibility(
      View.SYSTEM_UI_FLAG_FULLSCREEN |
      View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
      View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    );
    cockpit = new CockpitView(this);
    setContentView(cockpit);
  }

  @Override public void onWindowFocusChanged(boolean hasFocus){
    super.onWindowFocusChanged(hasFocus);
    if(hasFocus) getWindow().getDecorView().setSystemUiVisibility(
      View.SYSTEM_UI_FLAG_FULLSCREEN |
      View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
      View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    );
  }

  class CockpitView extends View {
    final float DW=1024f, DH=600f;
    final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    final int BG=Color.rgb(4,9,17);
    final int PANEL=Color.rgb(7,18,31);
    final int PANEL2=Color.rgb(9,23,40);
    final int BLUE=Color.rgb(49,138,255);
    final int ICE=Color.rgb(112,191,255);
    final int MUTED=Color.rgb(139,163,187);
    final int LINE=Color.rgb(24,80,145);
    final RectF navCard=new RectF(70,90,390,385);
    final RectF musicCard=new RectF(405,90,715,385);
    final RectF carCard=new RectF(730,90,985,385);
    final RectF[] bottom = new RectF[6];
    final String[] slots={"maps","music","carplay","vehicle","camera","settings"};
    final String[] labels={"MAPS","SPOTIFY","CARPLAY","VEHÍCULO","CÁMARA","AJUSTES"};
    float downX, downY; long downAt;
    Handler handler=new Handler();

    CockpitView(Context c){
      super(c);
      setLayerType(View.LAYER_TYPE_SOFTWARE,null);
      stroke.setStyle(Paint.Style.STROKE);
      stroke.setStrokeWidth(2);
      stroke.setColor(LINE);
      for(int i=0;i<6;i++) bottom[i]=new RectF(70+i*153,415,210+i*153,515);
      handler.post(new Runnable(){ public void run(){ invalidate(); handler.postDelayed(this,1000); }});
    }

    float sx(){ return getWidth()/DW; }
    float sy(){ return getHeight()/DH; }

    @Override protected void onDraw(Canvas c){
      super.onDraw(c);
      float s=Math.min(sx(),sy());
      float ox=(getWidth()-DW*s)/2f, oy=(getHeight()-DH*s)/2f;
      c.save(); c.translate(ox,oy); c.scale(s,s);

      c.drawColor(BG);
      drawAmbient(c);
      drawHeader(c);
      drawNav(c);
      drawMusic(c);
      drawVehicle(c);
      drawBottom(c);
      drawFooter(c);

      c.restore();
    }

    void drawAmbient(Canvas c){
      LinearGradient g=new LinearGradient(0,0,DW,DH,
        Color.rgb(4,9,17), Color.rgb(2,7,14), Shader.TileMode.CLAMP);
      p.setShader(g); c.drawRect(0,0,DW,DH,p); p.setShader(null);
      p.setColor(Color.argb(26,20,95,180));
      c.drawRoundRect(new RectF(45,60,1000,548),30,30,p);
    }

    void drawHeader(Canvas c){
      drawHyundai(c,72,43);
      text(c,"HYUNDAI",105,49,18,Color.WHITE,true,Paint.Align.LEFT);
      text(c,"•",198,49,17,MUTED,true,Paint.Align.LEFT);
      text(c,"ELANTRA 2016",218,49,18,ICE,true,Paint.Align.LEFT);
      text(c,"•  1.8L",350,49,18,Color.WHITE,true,Paint.Align.LEFT);

      p.setStyle(Paint.Style.FILL);
      p.setColor(Color.rgb(28,41,57));
      c.drawCircle(845,43,4,p); c.drawCircle(862,43,5,p); c.drawCircle(881,43,6,p);
      text(c,"BT",902,49,12,MUTED,true,Paint.Align.LEFT);
      text(c,new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date()),985,49,20,Color.WHITE,true,Paint.Align.RIGHT);
    }

    void drawHyundai(Canvas c,float x,float y){
      p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(2.6f); p.setColor(Color.WHITE);
      RectF r=new RectF(x-23,y-10,x+23,y+10); c.drawOval(r,p);
      Path h=new Path();
      h.moveTo(x-12,y+7); h.lineTo(x-4,y-7); h.moveTo(x+5,y+7); h.lineTo(x+13,y-7);
      h.moveTo(x-7,y+2); h.lineTo(x+8,y-2); c.drawPath(h,p);
      p.setStyle(Paint.Style.FILL);
    }

    void card(Canvas c,RectF r){
      p.setColor(PANEL); p.setShadowLayer(12,0,4,Color.argb(120,0,0,0));
      c.drawRoundRect(r,18,18,p); p.clearShadowLayer();
      stroke.setColor(LINE); stroke.setStrokeWidth(1.5f); c.drawRoundRect(r,18,18,stroke);
    }

    void drawNav(Canvas c){
      card(c,navCard);
      drawRealAppIcon(c,"com.google.android.apps.maps",92,111,34, fallbackColor("#4285F4"), "G");
      text(c,"Navegación",135,135,18,Color.WHITE,true,Paint.Align.LEFT);

      RectF map=new RectF(87,158,373,365);
      p.setColor(Color.rgb(8,25,44)); c.drawRoundRect(map,14,14,p);
      stroke.setColor(Color.rgb(15,47,78)); stroke.setStrokeWidth(1); c.drawRoundRect(map,14,14,stroke);

      p.setColor(Color.rgb(31,53,75)); p.setStrokeWidth(4); p.setStyle(Paint.Style.STROKE);
      Path roads=new Path();
      roads.moveTo(90,210); roads.cubicTo(150,190,210,240,370,205);
      roads.moveTo(110,335); roads.cubicTo(190,310,250,330,365,270);
      roads.moveTo(170,160); roads.lineTo(140,360);
      roads.moveTo(285,160); roads.lineTo(320,360);
      c.drawPath(roads,p);
      p.setColor(BLUE); p.setStrokeWidth(7);
      Path route=new Path(); route.moveTo(292,350); route.cubicTo(265,315,285,260,246,225); route.cubicTo(230,208,230,185,242,164); c.drawPath(route,p);
      p.setStyle(Paint.Style.FILL);

      p.setColor(BLUE); c.drawCircle(292,350,18,p);
      Path arrow=new Path(); arrow.moveTo(292,338); arrow.lineTo(304,359); arrow.lineTo(292,354); arrow.lineTo(280,359); arrow.close();
      p.setColor(Color.WHITE); c.drawPath(arrow,p);
      text(c,"Av. Principal",106,347,14,Color.WHITE,true,Paint.Align.LEFT);
    }

    void drawMusic(Canvas c){
      card(c,musicCard);
      drawRealAppIcon(c,"com.spotify.music",427,111,34,Color.rgb(30,215,96),"S");
      text(c,"Música",470,135,18,Color.WHITE,true,Paint.Align.LEFT);

      RectF art=new RectF(500,165,620,285);
      LinearGradient gg=new LinearGradient(500,165,620,285,Color.rgb(40,80,150),Color.rgb(170,80,120),Shader.TileMode.CLAMP);
      p.setShader(gg); c.drawRoundRect(art,12,12,p); p.setShader(null);
      p.setColor(Color.argb(100,0,0,0)); c.drawRect(500,245,620,285,p);
      text(c,"Sin reproducción",560,315,17,Color.WHITE,true,Paint.Align.CENTER);
      text(c,"Spotify",560,338,13,MUTED,false,Paint.Align.CENTER);

      p.setColor(Color.rgb(29,72,112)); c.drawRoundRect(new RectF(445,355,675,359),3,3,p);
      p.setColor(BLUE); c.drawRoundRect(new RectF(445,355,525,359),3,3,p);
      text(c,"◀",475,381,23,Color.WHITE,true,Paint.Align.CENTER);
      p.setColor(BLUE); c.drawCircle(560,372,25,p);
      text(c,"▶",560,380,20,Color.WHITE,true,Paint.Align.CENTER);
      text(c,"▶",645,381,23,Color.WHITE,true,Paint.Align.CENTER);
    }

    void drawVehicle(Canvas c){
      card(c,carCard);
      drawCarIcon(c,752,120);
      text(c,"Vehículo",790,135,18,Color.WHITE,true,Paint.Align.LEFT);
      stat(c,"TEMP. MOTOR","-- °C",755,190);
      stat(c,"BATERÍA","--.- V",755,258);
      stat(c,"RPM","----",755,326);
      text(c,"OBD2",952,360,11,MUTED,true,Paint.Align.RIGHT);
    }

    void stat(Canvas c,String a,String b,float x,float y){
      text(c,a,x,y,12,MUTED,true,Paint.Align.LEFT);
      text(c,b,x,y+28,22,Color.WHITE,true,Paint.Align.LEFT);
    }

    void drawBottom(Canvas c){
      for(int i=0;i<6;i++){
        RectF r=bottom[i]; p.setColor(PANEL2); c.drawRoundRect(r,15,15,p);
        stroke.setColor(LINE); stroke.setStrokeWidth(1.3f); c.drawRoundRect(r,15,15,stroke);
        String slot=slots[i];
        if(slot.equals("maps")) drawRealAppIcon(c,"com.google.android.apps.maps",r.centerX()-18,r.top+16,36,fallbackColor("#4285F4"),"G");
        else if(slot.equals("music")) drawRealAppIcon(c,"com.spotify.music",r.centerX()-18,r.top+16,36,Color.rgb(30,215,96),"S");
        else if(slot.equals("carplay")) drawCarPlay(c,r.centerX(),r.top+34);
        else if(slot.equals("vehicle")) drawCarIcon(c,r.centerX(),r.top+34);
        else if(slot.equals("camera")) drawCamera(c,r.centerX(),r.top+34);
        else drawGear(c,r.centerX(),r.top+34);
        text(c,labels[i],r.centerX(),r.bottom-16,13,Color.WHITE,true,Paint.Align.CENTER);
      }
    }

    void drawFooter(Canvas c){
      text(c,"RADIO",95,560,12,ICE,true,Paint.Align.LEFT);
      text(c,"DVR",205,560,12,ICE,true,Paint.Align.LEFT);
      text(c,"Mantén presionado CarPlay / Cámara para asignar la app",985,560,11,MUTED,false,Paint.Align.RIGHT);
    }

    void drawRealAppIcon(Canvas c,String pkg,float x,float y,int size,int color,String letter){
      try{
        Drawable d=getPackageManager().getApplicationIcon(pkg);
        d.setBounds((int)x,(int)y,(int)(x+size),(int)(y+size)); d.draw(c); return;
      }catch(Exception e){}
      p.setColor(color); c.drawCircle(x+size/2f,y+size/2f,size/2f,p);
      text(c,letter,x+size/2f,y+size*0.75f,size*0.55f,Color.WHITE,true,Paint.Align.CENTER);
    }

    void drawCarPlay(Canvas c,float x,float y){
      String comp=getPreferences(MODE_PRIVATE).getString("slot_carplay",null);
      if(comp!=null){
        try{
          ComponentName cn=ComponentName.unflattenFromString(comp);
          Drawable d=getPackageManager().getApplicationIcon(cn.getPackageName());
          d.setBounds((int)(x-18),(int)(y-18),(int)(x+18),(int)(y+18)); d.draw(c); return;
        }catch(Exception e){}
      }
      p.setColor(Color.rgb(36,220,50)); c.drawRoundRect(new RectF(x-19,y-19,x+19,y+19),8,8,p);
      p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(3); p.setColor(Color.WHITE);
      c.drawArc(new RectF(x-11,y-11,x+11,y+11),40,280,false,p); p.setStyle(Paint.Style.FILL);
      Path tri=new Path(); tri.moveTo(x-2,y-8); tri.lineTo(x+10,y); tri.lineTo(x-2,y+8); tri.close(); c.drawPath(tri,p);
    }

    void drawCarIcon(Canvas c,float x,float y){
      p.setColor(ICE); RectF body=new RectF(x-17,y-6,x+17,y+8); c.drawRoundRect(body,5,5,p);
      Path roof=new Path(); roof.moveTo(x-11,y-6); roof.lineTo(x-5,y-14); roof.lineTo(x+8,y-14); roof.lineTo(x+14,y-6); roof.close(); c.drawPath(roof,p);
      p.setColor(BG); c.drawCircle(x-10,y+9,4,p); c.drawCircle(x+10,y+9,4,p);
    }

    void drawCamera(Canvas c,float x,float y){
      p.setColor(ICE); c.drawRoundRect(new RectF(x-18,y-12,x+18,y+13),6,6,p);
      p.setColor(BG); c.drawCircle(x,y,8,p);
      p.setColor(ICE); c.drawRect(x-8,y-17,x+3,y-12,p);
    }

    void drawGear(Canvas c,float x,float y){
      p.setColor(ICE); c.drawCircle(x,y,15,p); p.setColor(BG); c.drawCircle(x,y,6,p);
      p.setColor(ICE);
      for(int i=0;i<8;i++){ double a=i*Math.PI/4; float dx=(float)Math.cos(a)*18,dy=(float)Math.sin(a)*18; c.drawRect(x+dx-3,y+dy-3,x+dx+3,y+dy+3,p); }
    }

    void text(Canvas c,String s,float x,float y,float size,int color,boolean bold,Paint.Align align){
      p.setShader(null); p.setStyle(Paint.Style.FILL); p.setColor(color); p.setTextSize(size); p.setTextAlign(align);
      p.setTypeface(bold?Typeface.create("sans-serif",Typeface.BOLD):Typeface.create("sans-serif",Typeface.NORMAL));
      c.drawText(s,x,y,p);
    }

    int fallbackColor(String hex){ return Color.parseColor(hex); }

    @Override public boolean onTouchEvent(android.view.MotionEvent e){
      float s=Math.min(sx(),sy());
      float ox=(getWidth()-DW*s)/2f, oy=(getHeight()-DH*s)/2f;
      float x=(e.getX()-ox)/s, y=(e.getY()-oy)/s;
      if(e.getAction()==MotionEvent.ACTION_DOWN){ downX=x; downY=y; downAt=System.currentTimeMillis(); return true; }
      if(e.getAction()==MotionEvent.ACTION_UP){
        long dt=System.currentTimeMillis()-downAt;
        String slot=hit(x,y);
        if(slot!=null){
          if(dt>650 && (slot.equals("carplay")||slot.equals("camera")||slot.equals("radio")||slot.equals("dvr"))) choose(slot);
          else launch(slot);
        }
        return true;
      }
      return true;
    }

    String hit(float x,float y){
      if(navCard.contains(x,y)) return "maps";
      if(musicCard.contains(x,y)) return "music";
      if(carCard.contains(x,y)) return "vehicle";
      for(int i=0;i<6;i++) if(bottom[i].contains(x,y)) return slots[i];
      if(y>530 && x<160) return "radio";
      if(y>530 && x>=160 && x<270) return "dvr";
      return null;
    }

    void choose(final String slot){
      PackageManager pm=getPackageManager();
      Intent q=new Intent(Intent.ACTION_MAIN); q.addCategory(Intent.CATEGORY_LAUNCHER);
      List<ResolveInfo> all=pm.queryIntentActivities(q,0); final ArrayList<ResolveInfo> apps=new ArrayList<>();
      for(ResolveInfo r:all) if(r.activityInfo!=null&&!getPackageName().equals(r.activityInfo.packageName)) apps.add(r);
      Collections.sort(apps,(a,b)->a.loadLabel(pm).toString().compareToIgnoreCase(b.loadLabel(pm).toString()));
      String[] names=new String[apps.size()]; for(int i=0;i<apps.size();i++) names[i]=apps.get(i).loadLabel(pm).toString();
      new AlertDialog.Builder(MainActivity.this).setTitle("Asignar "+slot.toUpperCase(Locale.ROOT)).setItems(names,(d,w)->{
        ResolveInfo r=apps.get(w);
        String comp=r.activityInfo.packageName+"/"+r.activityInfo.name;
        getPreferences(MODE_PRIVATE).edit().putString("slot_"+slot,comp).apply();
        Toast.makeText(MainActivity.this,"Asignado: "+names[w],Toast.LENGTH_SHORT).show();
        invalidate();
      }).setNegativeButton("Cancelar",null).show();
    }

    void launch(String slot){
      if(slot.equals("settings")){ startActivity(new Intent(Settings.ACTION_SETTINGS)); return; }
      if(slot.equals("vehicle")){
        new AlertDialog.Builder(MainActivity.this).setTitle("Vehículo • OBD2")
          .setMessage("TEMP. MOTOR   -- °C\nBATERÍA       --.- V\nRPM           ----\n\nLa v0.3 conectará estos datos al OBD2.")
          .setPositiveButton("OK",null).show(); return;
      }
      String saved=getPreferences(MODE_PRIVATE).getString("slot_"+slot,null);
      if(saved!=null){
        try{
          ComponentName cn=ComponentName.unflattenFromString(saved);
          Intent i=new Intent(Intent.ACTION_MAIN); i.addCategory(Intent.CATEGORY_LAUNCHER); i.setComponent(cn); startActivity(i); return;
        }catch(Exception e){}
      }
      String[] pkgs = slot.equals("maps") ? new String[]{"com.google.android.apps.maps","com.waze"} :
        slot.equals("music") ? new String[]{"com.spotify.music","com.google.android.apps.youtube.music"} :
        slot.equals("dvr") ? new String[]{"com.vidure.veecar"} : new String[]{};
      for(String pkg:pkgs){
        Intent i=getPackageManager().getLaunchIntentForPackage(pkg);
        if(i!=null){ startActivity(i); return; }
      }
      if(slot.equals("carplay")||slot.equals("camera")||slot.equals("radio")||slot.equals("dvr")) choose(slot);
    }
  }
}
