package com.elantra.smartcockpit;

import android.app.*;
import android.os.*;
import android.provider.Settings;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.*;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
  @Override public void onCreate(Bundle b){
    super.onCreate(b);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    getWindow().getDecorView().setSystemUiVisibility(5894);
    setContentView(new CockpitView(this));
  }

  class CockpitView extends View {
    final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    final Bitmap bg;
    final RectF mapsTop=new RectF(118,78,454,448);
    final RectF musicTop=new RectF(462,78,748,448);
    final RectF vehicleTop=new RectF(758,78,1018,448);
    final RectF[] buttons={
      new RectF(120,458,260,590),new RectF(269,458,411,590),new RectF(420,458,563,590),
      new RectF(575,458,718,590),new RectF(728,458,870,590),new RectF(879,458,1022,590)
    };
    final String[] slots={"maps","music","carplay","vehicle","camera","settings"};
    long downAt; float downX,downY;
    Handler h=new Handler();

    CockpitView(Context c){
      super(c);
      byte[] raw=Base64.decode("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",Base64.DEFAULT);
      bg=BitmapFactory.decodeByteArray(raw,0,raw.length);
      h.post(new Runnable(){ public void run(){ invalidate(); h.postDelayed(this,1000); }});
    }

    @Override protected void onDraw(Canvas c){
      super.onDraw(c);
      c.drawBitmap(bg,null,new Rect(0,0,getWidth(),getHeight()),p);
      float sx=getWidth()/1024f, sy=getHeight()/600f;
      c.save(); c.scale(sx,sy);

      // Replace static time with live time while preserving mockup styling.
      p.setColor(Color.rgb(2,8,18)); c.drawRect(915,18,1024,70,p);
      p.setColor(Color.WHITE); p.setTextSize(27); p.setTypeface(Typeface.create("sans-serif",Typeface.NORMAL));
      p.setTextAlign(Paint.Align.RIGHT);
      c.drawText(new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date()),1002,55,p);

      // Use actual installed application icons on top of the approved mockup.
      drawInstalledIcon(c,"com.google.android.apps.maps",158,480,55);
      drawInstalledIcon(c,"com.spotify.music",307,480,55);
      drawAssignedIcon(c,"carplay",458,480,55);
      drawAssignedIcon(c,"camera",765,480,55);

      c.restore();
    }

    void drawInstalledIcon(Canvas c,String pkg,float x,float y,int size){
      try{
        Drawable d=getPackageManager().getApplicationIcon(pkg);
        d.setBounds((int)x,(int)y,(int)(x+size),(int)(y+size)); d.draw(c);
      }catch(Exception ignored){}
    }

    void drawAssignedIcon(Canvas c,String slot,float x,float y,int size){
      String saved=getPreferences(MODE_PRIVATE).getString("slot_"+slot,null);
      if(saved==null)return;
      try{
        ComponentName cn=ComponentName.unflattenFromString(saved);
        Drawable d=getPackageManager().getApplicationIcon(cn.getPackageName());
        p.setColor(Color.rgb(5,15,32)); c.drawRoundRect(new RectF(x-5,y-5,x+size+5,y+size+5),12,12,p);
        d.setBounds((int)x,(int)y,(int)(x+size),(int)(y+size)); d.draw(c);
      }catch(Exception ignored){}
    }

    @Override public boolean onTouchEvent(MotionEvent e){
      float x=e.getX()*1024f/getWidth(), y=e.getY()*600f/getHeight();
      if(e.getAction()==MotionEvent.ACTION_DOWN){ downAt=System.currentTimeMillis(); downX=x; downY=y; return true; }
      if(e.getAction()==MotionEvent.ACTION_UP){
        String slot=hit(x,y); long dt=System.currentTimeMillis()-downAt;
        if(slot!=null){
          if(dt>650 && (slot.equals("carplay")||slot.equals("camera"))) choose(slot);
          else launch(slot);
        } else if(dt>1500 && x<330 && y<80){
          try{startActivity(new Intent(Settings.ACTION_HOME_SETTINGS));}catch(Exception ex){startActivity(new Intent(Settings.ACTION_SETTINGS));}
        }
        return true;
      }
      return true;
    }

    String hit(float x,float y){
      if(mapsTop.contains(x,y))return "maps";
      if(musicTop.contains(x,y))return "music";
      if(vehicleTop.contains(x,y))return "vehicle";
      for(int i=0;i<buttons.length;i++)if(buttons[i].contains(x,y))return slots[i];
      return null;
    }

    void launch(String slot){
      if(slot.equals("settings")){startActivity(new Intent(Settings.ACTION_SETTINGS));return;}
      if(slot.equals("vehicle")){
        new AlertDialog.Builder(MainActivity.this).setTitle("Vehículo")
          .setMessage("TEMP. MOTOR   -- °C\nBATERÍA       --.- V\nRPM           ----\n\nEstos datos se activarán cuando conectemos el OBD2.")
          .setPositiveButton("OK",null).show(); return;
      }
      String saved=getPreferences(MODE_PRIVATE).getString("slot_"+slot,null);
      if(saved!=null){
        try{
          ComponentName cn=ComponentName.unflattenFromString(saved);
          Intent i=new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setComponent(cn);
          startActivity(i); return;
        }catch(Exception ignored){}
      }
      String[] pkgs=slot.equals("maps")?new String[]{"com.google.android.apps.maps","com.waze"}:
        slot.equals("music")?new String[]{"com.spotify.music","com.google.android.apps.youtube.music"}:new String[]{};
      for(String pkg:pkgs){
        Intent i=getPackageManager().getLaunchIntentForPackage(pkg);
        if(i!=null){startActivity(i);return;}
      }
      if(slot.equals("carplay")||slot.equals("camera"))choose(slot);
    }

    void choose(final String slot){
      PackageManager pm=getPackageManager();
      Intent q=new Intent(Intent.ACTION_MAIN); q.addCategory(Intent.CATEGORY_LAUNCHER);
      List<ResolveInfo> all=pm.queryIntentActivities(q,0); final ArrayList<ResolveInfo> apps=new ArrayList<>();
      for(ResolveInfo r:all)if(r.activityInfo!=null&&!getPackageName().equals(r.activityInfo.packageName))apps.add(r);
      Collections.sort(apps,(a,b)->a.loadLabel(pm).toString().compareToIgnoreCase(b.loadLabel(pm).toString()));
      String[] names=new String[apps.size()];
      for(int i=0;i<apps.size();i++)names[i]=apps.get(i).loadLabel(pm).toString();
      new AlertDialog.Builder(MainActivity.this).setTitle("Asignar "+slot.toUpperCase(Locale.ROOT))
        .setItems(names,(d,w)->{
          ResolveInfo r=apps.get(w);
          getPreferences(MODE_PRIVATE).edit().putString("slot_"+slot,r.activityInfo.packageName+"/"+r.activityInfo.name).apply();
          Toast.makeText(MainActivity.this,"Asignado: "+names[w],Toast.LENGTH_SHORT).show();
          invalidate();
        }).setNegativeButton("Cancelar",null).show();
    }
  }
}