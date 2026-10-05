
    }
}
package com.wallora.home;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {

    private static final int GOLD = Color.rgb(214,168,74);
    private static final int GOLD_LIGHT = Color.rgb(255,216,145);
    private static final int BG = Color.rgb(7,7,7);
    private static final int PANEL = Color.argb(225, 15,15,15);
    private static final int PANEL_2 = Color.argb(235, 28,28,28);
    private static final int MUTED = Color.rgb(185,185,185);
    private static final int GREEN = Color.rgb(102,210,110);

    private static final int PICK_CLIENT_PHOTO = 201;
    private static final int PICK_IDEA_PHOTO = 202;
    private static final int PICK_BEFORE_PHOTO = 203;
    private static final int PICK_AFTER_PHOTO = 204;

    private final DecimalFormat df = new DecimalFormat("0.##");
    private SharedPreferences prefs;
    private boolean tablet;
    private boolean powerOn;
    private boolean expanded;
    private String lang = "uk";

    private LinearLayout page;
    private FrameLayout rootFrame;
    private float downY;
    private boolean handlingGesture = false;

    private Uri pendingClientPhoto;
    private Uri pendingIdeaPhoto;
    private Uri pendingBeforePhoto;
    private Uri pendingAfterPhoto;

    private ImageView clientPhotoPreview;
    private ImageView ideaPreview;
    private ImageView beforePreview;
    private ImageView afterPreview;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("wallora", MODE_PRIVATE);
        tablet = getResources().getConfiguration().smallestScreenWidthDp >= 600;
        lang = prefs.getString("lang","uk");
        powerOn = prefs.getBoolean("power_on", false);
        expanded = prefs.getBoolean("expanded", false);
        immersive();
        if(powerOn) showDashboard(); else showPowerScreen();
    }

    @Override public void onResume() {
        super.onResume();
        immersive();
    }

    private void immersive() {
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private String tr(String uk, String cz, String en) {
        if("cz".equals(lang)) return cz;
        if("en".equals(lang)) return en;
        return uk;
    }

    private int dp(int n){
        return (int)(n*getResources().getDisplayMetrics().density+0.5f);
    }

    private GradientDrawable bg(int color, int radius, int strokeColor, int stroke){
        GradientDrawable g=new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(dp(radius));
        if(stroke>0) g.setStroke(dp(stroke), strokeColor);
        return g;
    }

    private TextView tv(String s,int sp,int color){
        TextView t=new TextView(this);
        t.setText(s);
        t.setTextSize(sp);
        t.setTextColor(color);
        t.setPadding(dp(2),dp(2),dp(2),dp(2));
        return t;
    }

    private TextView label(String s){
        TextView t=tv(s,12,GOLD_LIGHT);
        t.setAllCaps(true);
        t.setLetterSpacing(.12f);
        return t;
    }

    private Space space(int h){
        Space s=new Space(this);
        s.setLayoutParams(new LinearLayout.LayoutParams(1,dp(h)));
        return s;
    }

    private Button button(String text){
        Button b=new Button(this);
        b.setText(text);
        b.setTextColor(Color.WHITE);
        b.setTextSize(14);
        b.setAllCaps(false);
        b.setGravity(Gravity.CENTER);
        b.setBackground(bg(PANEL_2,14,GOLD,1));
        b.setPadding(dp(12),dp(10),dp(12),dp(10));
        return b;
    }

    private Button goldButton(String text){
        Button b=button(text);
        b.setTextColor(Color.BLACK);
        b.setTypeface(Typeface.DEFAULT_BOLD);
        b.setBackground(bg(GOLD_LIGHT,16,GOLD_LIGHT,1));
        return b;
    }

    private EditText edit(String hint){
        EditText e=new EditText(this);
        e.setHint(hint);
        e.setHintTextColor(Color.rgb(120,120,120));
        e.setTextColor(Color.WHITE);
        e.setSingleLine(true);
        e.setPadding(dp(14),dp(12),dp(14),dp(12));
        e.setBackground(bg(PANEL_2,12,Color.rgb(70,70,70),1));
        return e;
    }

    private EditText number(String hint){
        EditText e=edit(hint);
        e.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        return e;
    }

    private LinearLayout card(){
        LinearLayout c=new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(dp(16),dp(16),dp(16),dp(16));
        c.setBackground(bg(PANEL,18,Color.rgb(88,67,30),1));
        return c;
    }

    private void addCard(View v){
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);
        lp.setMargins(0,0,0,dp(12));
        page.addView(v,lp);
    }

    private LinearLayout row(){
        LinearLayout r=new LinearLayout(this);
        r.setOrientation(LinearLayout.HORIZONTAL);
        r.setGravity(Gravity.CENTER_VERTICAL);
        return r;
    }

    private void applyBackground(View v){
        try {
            int id=getResources().getIdentifier("wallora_bg","drawable",getPackageName());
            if(id!=0) v.setBackgroundResource(id);
            else v.setBackgroundColor(BG);
        } catch(Exception e){
            v.setBackgroundColor(BG);
        }
    }

    private ScrollView shell(String titleText, boolean back){
        tablet = getResources().getConfiguration().smallestScreenWidthDp >= 600;

        rootFrame=new FrameLayout(this);
        applyBackground(rootFrame);

        LinearLayout overlay=new LinearLayout(this);
        overlay.setOrientation(LinearLayout.VERTICAL);
        overlay.setPadding(dp(tablet?26:14),dp(14),dp(tablet?26:14),dp(26));
        overlay.setBackgroundColor(Color.argb(80,0,0,0));

        LinearLayout top=row();

        if(back){
            Button home=button("‹ WALLORA");
            home.setOnClickListener(v->showDashboard());
            top.addView(home,new LinearLayout.LayoutParams(tablet?dp(170):dp(118),dp(48)));
        }

        TextView title=tv(titleText,tablet?27:20,GOLD_LIGHT);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams tlp=new LinearLayout.LayoutParams(0,dp(54),1);
        tlp.setMargins(dp(10),0,0,0);
        top.addView(title,tlp);

        Button langBtn=button("🌐 "+lang.toUpperCase());
        langBtn.setOnClickListener(v->showLanguageDialog());
        top.addView(langBtn,new LinearLayout.LayoutParams(dp(84),dp(46)));

        Button settings=button("⚙");
        settings.setOnClickListener(v->showSettings());
        LinearLayout.LayoutParams slp=new LinearLayout.LayoutParams(dp(54),dp(46));
        slp.setMargins(dp(6),0,0,0);
        top.addView(settings,slp);

        overlay.addView(top);
        overlay.addView(space(10));

        page=new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        overlay.addView(page,new LinearLayout.LayoutParams(-1,-2));

        ScrollView sv=new ScrollView(this);
        sv.setFillViewport(true);
        sv.addView(overlay);
        rootFrame.addView(sv,new FrameLayout.LayoutParams(-1,-1));

        installGestures(rootFrame);
        setContentView(rootFrame);
        return sv;
    }

    private void installGestures(View v){
        v.setOnTouchListener((view,event)->{
            switch(event.getActionMasked()){
                case MotionEvent.ACTION_DOWN:
                    downY=event.getY();
                    handlingGesture=false;
                    return false;
                case MotionEvent.ACTION_MOVE:
                    if(Math.abs(event.getY()-downY)>dp(35)) handlingGesture=true;
                    return false;
                case MotionEvent.ACTION_UP:
                    float dy=event.getY()-downY;
                    if(Math.abs(dy)>dp(90)){
                        if(dy<0){
                            if(!powerOn){
                                powerOn=true;
                                expanded=true;
                                saveUiState();
                                restoreBrightness();
                                showDashboard();
                            }else if(!expanded){
                                expanded=true;
                                saveUiState();
                                showDashboard();
                            }
                        }else{
                            if(powerOn && expanded){
                                expanded=false;
                                saveUiState();
                                showDashboard();
                            }
                        }
                    }
                    handlingGesture=false;
                    return false;
            }
            return false;
        });
    }

    private void saveUiState(){
        prefs.edit().putBoolean("power_on",powerOn).putBoolean("expanded",expanded).apply();
    }

    private void showPowerScreen(){
        powerOn=false;
        expanded=false;
        saveUiState();
        restoreBrightness();

        rootFrame=new FrameLayout(this);
        applyBackground(rootFrame);

        LinearLayout dark=new LinearLayout(this);
        dark.setOrientation(LinearLayout.VERTICAL);
        dark.setGravity(Gravity.CENTER);
        dark.setPadding(dp(24),dp(28),dp(24),dp(28));
        dark.setBackgroundColor(Color.argb(80,0,0,0));

        TextView logo=tv("WALLORA", tablet?48:38, GOLD_LIGHT);
        logo.setTypeface(Typeface.DEFAULT_BOLD);
        logo.setLetterSpacing(.14f);
        logo.setGravity(Gravity.CENTER);
        dark.addView(logo);

        TextView sub=tv("PRINT YOUR WORLD",14,GOLD_LIGHT);
        sub.setLetterSpacing(.18f);
        sub.setGravity(Gravity.CENTER);
        dark.addView(sub);

        dark.addView(space(tablet?70:45));

        TextView printer=tv("▥",tablet?120:90,GOLD_LIGHT);
        printer.setGravity(Gravity.CENTER);
        dark.addView(printer);

        dark.addView(space(24));

        Button power=goldButton("⏻");
        power.setTextSize(tablet?42:34);
        power.setOnClickListener(v->{
            powerOn=true;
            expanded=true;
            saveUiState();
            showDashboard();
        });
        LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(tablet?dp(160):dp(135),tablet?dp(160):dp(135));
        pp.gravity=Gravity.CENTER_HORIZONTAL;
        dark.addView(power,pp);

        dark.addView(space(18));

        TextView on=tv(tr("УВІМКНУТИ ПРИНТЕР","ZAPNOUT TISKÁRNU","TURN ON PRINTER"),18,GOLD_LIGHT);
        on.setTypeface(Typeface.DEFAULT_BOLD);
        on.setGravity(Gravity.CENTER);
        dark.addView(on);

        TextView hint=tv(tr("Натисніть для запуску або проведіть вгору","Klepněte nebo přejeďte nahoru","Tap or swipe up"),12,MUTED);
        hint.setGravity(Gravity.CENTER);
        dark.addView(hint);

        LinearLayout bottom=row();
        TextView status=tv("●  "+tr("Принтер офлайн","Tiskárna offline","Printer offline"),12,GREEN);
        bottom.addView(status,new LinearLayout.LayoutParams(0,dp(48),1));

        Button settings=button("⚙ "+tr("Налаштування","Nastavení","Settings"));
        settings.setOnClickListener(v->showSettings());
        bottom.addView(settings,new LinearLayout.LayoutParams(tablet?dp(220):dp(170),dp(46)));

        dark.addView(space(30));
        dark.addView(bottom);

        rootFrame.addView(dark,new FrameLayout.LayoutParams(-1,-1));
        installGestures(rootFrame);
        setContentView(rootFrame);
    }

    private void showDashboard(){
        powerOn=true;
        saveUiState();
        shell("WALLORA",false);

        if(!expanded){
            LinearLayout compact=card();
            compact.setGravity(Gravity.CENTER);
            TextView logo=tv("WALLORA",tablet?48:38,GOLD_LIGHT);
            logo.setTypeface(Typeface.DEFAULT_BOLD);
            logo.setGravity(Gravity.CENTER);
            compact.addView(logo);
            TextView sub=tv("PRINT YOUR WORLD",13,GOLD_LIGHT);
            sub.setLetterSpacing(.18f);
            sub.setGravity(Gravity.CENTER);
            compact.addView(sub);
            compact.addView(space(18));
            TextView status=tv("● ONLINE",16,GREEN);
            status.setGravity(Gravity.CENTER);
            compact.addView(status);
            compact.addView(space(18));
            TextView hint=tv(tr("Проведіть вгору, щоб відкрити панель","Přejeďte nahoru pro otevření panelu","Swipe up to open control panel"),14,MUTED);
            hint.setGravity(Gravity.CENTER);
            compact.addView(hint);
            addCard(compact);
            return;
        }

        addCurrentOrderCard();
        addWallParamsCard();

        String[][] items={
                {"▣",tr("ДРУК","TISK","PRINT"),tr("Новий проєкт","Nový projekt","New project")},
                {"▧",tr("ГАЛЕРЕЯ","GALERIE","GALLERY"),tr("Галерея робіт","Galerie prací","Work gallery")},
                {"◉",tr("КЛІЄНТИ","KLIENTI","CLIENTS"),tr("Клієнти й фото","Klienti a fotky","Clients & photos")},
                {"▰",tr("ФАЙЛИ","SOUBORY","FILES"),tr("Файли проєкту","Soubory projektu","Project files")},
                {"▣",tr("ВІДДАЛЕНИЙ ПК","VZDÁLENÝ PC","REMOTE PC"),tr("Керування ПК","Ovládání PC","PC control")},
                {"◎",tr("СОЦМЕРЕЖІ","SOCIÁLNÍ SÍTĚ","SOCIAL"),tr("Instagram / TikTok","Instagram / TikTok","Instagram / TikTok")}
        };

        GridLayout grid=new GridLayout(this);
        grid.setColumnCount(tablet?3:2);
        for(int i=0;i<items.length;i++){
            final int ix=i;
            LinearLayout c=card();
            c.setGravity(Gravity.CENTER);
            if(i==0) c.setBackground(bg(Color.argb(220,70,45,10),18,GOLD_LIGHT,2));
            TextView icon=tv(items[i][0],tablet?34:28,GOLD_LIGHT);
            icon.setGravity(Gravity.CENTER);
            c.addView(icon);
            TextView n=tv(items[i][1],tablet?16:14,Color.WHITE);
            n.setTypeface(Typeface.DEFAULT_BOLD);
            n.setGravity(Gravity.CENTER);
            c.addView(n);
            TextView d=tv(items[i][2],11,MUTED);
            d.setGravity(Gravity.CENTER);
            c.addView(d);
            c.setOnClickListener(v->mainAction(ix));

            GridLayout.LayoutParams gp=new GridLayout.LayoutParams();
            gp.width=0;
            gp.height=dp(tablet?128:118);
            gp.columnSpec=GridLayout.spec(GridLayout.UNDEFINED,1,1f);
            gp.setMargins(dp(5),dp(5),dp(5),dp(5));
            grid.addView(c,gp);
        }
        page.addView(grid,new LinearLayout.LayoutParams(-1,-2));

        addCard(makeDock());

        Button off=button("⏻ "+tr("Вимкнути інтерфейс","Vypnout rozhraní","Power off interface"));
        off.setOnClickListener(v->enterSleepMode());
        addCard(off);

        TextView swipe=tv("↓ "+tr("Свайп вниз — згорнути панель","Přejetí dolů — sbalit panel","Swipe down — collapse panel"),12,MUTED);
        swipe.setGravity(Gravity.CENTER);
        addCard(swipe);
    }

    private void addCurrentOrderCard(){
        ArrayList<String> orders=loadList("orders");
        LinearLayout c=card();
        c.addView(label(tr("ПОТОЧНЕ ЗАМОВЛЕННЯ","AKTUÁLNÍ ZAKÁZKA","CURRENT ORDER")));
        c.addView(space(6));

        if(orders.isEmpty()){
            TextView none=tv(tr("Немає активного замовлення","Žádná aktivní zakázka","No active order"),18,Color.WHITE);
            none.setTypeface(Typeface.DEFAULT_BOLD);
            c.addView(none);
            c.addView(tv(tr("Створіть замовлення — тут з’являться фото, статус, розмір і ціна.","Vytvořte zakázku — zde se zobrazí fotky, stav, rozměr a cena.","Create an order — photos, status, size and price will appear here."),12,MUTED));
            Button add=goldButton("＋ "+tr("Нове замовлення","Nová zakázka","New order"));
            add.setOnClickListener(v->orderDialog(null,-1));
            c.addView(space(8)); c.addView(add);
        } else {
            int idx=orders.size()-1;
            String[] p=parts(orders.get(idx),13);
            TextView n=tv(p[0],20,GOLD_LIGHT); n.setTypeface(Typeface.DEFAULT_BOLD); c.addView(n);
            c.addView(tv(p[1]+"  •  "+p[2]+" × "+p[3]+" cm  •  "+p[4]+" m²",13,Color.WHITE));
            c.addView(tv(tr("Ціна: ","Cena: ","Price: ")+p[5]+" Kč",14,GOLD_LIGHT));
            c.addView(space(8));

            LinearLayout statuses=row();
            String[] values={"active","progress","done"};
            String[] labels={
                    tr("Активне","Aktivní","Active"),
                    tr("Виконується","Probíhá","In progress"),
                    tr("Завершено","Dokončeno","Completed")
            };
            for(int i=0;i<3;i++){
                final String val=values[i];
                Button b=button(labels[i]);
                if(val.equals(p[6])) b.setBackground(bg(Color.argb(230,90,60,12),14,GOLD_LIGHT,2));
                b.setOnClickListener(v->{
                    ArrayList<String> list=loadList("orders");
                    String[] pp=parts(list.get(idx),13);
                    pp[6]=val;
                    list.set(idx,join(pp));
                    saveList("orders",list);
                    showDashboard();
                });
                LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,dp(48),1);
                if(i>0) lp.setMargins(dp(5),0,0,0);
                statuses.addView(b,lp);
            }
            c.addView(statuses);
            c.addView(space(8));

            Button open=button(tr("Відкрити деталі замовлення","Otevřít detail zakázky","Open order details"));
            final int orderIndex=idx;
            open.setOnClickListener(v->showOrderDetails(orderIndex));
            c.addView(open);
        }
        addCard(c);
    }

    private void addWallParamsCard(){
        LinearLayout c=card();
        c.addView(label(tr("ПАРАМЕТРИ СТІНИ","PARAMETRY STĚNY","WALL PARAMETERS")));
        c.addView(space(6));
        String[][] vals={
                {tr("Ширина","Šířka","Width"),prefs.getString("wall_w","320")+" cm"},
                {tr("Висота","Výška","Height"),prefs.getString("wall_h","250")+" cm"},
                {tr("Площа","Plocha","Area"),prefs.getString("wall_area","8.0")+" m²"},
                {tr("Відстань","Vzdálenost","Distance"),prefs.getString("wall_dist","5")+" mm"}
        };
        for(String[] x:vals){
            LinearLayout r=row();
            TextView l=tv(x[0],14,MUTED);
            TextView v=tv(x[1],15,Color.WHITE);
            v.setTypeface(Typeface.DEFAULT_BOLD);
            v.setGravity(Gravity.RIGHT);
            r.addView(l,new LinearLayout.LayoutParams(0,dp(40),1));
            r.addView(v,new LinearLayout.LayoutParams(dp(130),dp(40)));
            c.addView(r);
        }
        c.setOnClickListener(v->wallParamsDialog());
        addCard(c);
    }

    private LinearLayout makeDock(){
        LinearLayout dock=card();
        dock.setOrientation(LinearLayout.HORIZONTAL);
        String[][] apps={
                {"B","BetterPrint","com.anydesk.anydeskandroid"},
                {"U","UltraPrint","com.teamviewer.teamviewer.market.mobile"},
                {"A","AnyDesk","com.anydesk.anydeskandroid"},
                {"T","TeamViewer","com.teamviewer.teamviewer.market.mobile"},
                {"D","Drive","com.google.android.apps.docs"}
        };
        for(String[] a:apps){
            LinearLayout item=new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(Gravity.CENTER);
            TextView ic=tv(a[0],22,GOLD_LIGHT); ic.setTypeface(Typeface.DEFAULT_BOLD); ic.setGravity(Gravity.CENTER);
            TextView tx=tv(a[1],9,MUTED); tx.setGravity(Gravity.CENTER);
            item.addView(ic); item.addView(tx);
            item.setOnClickListener(v->launchAny(a[2]));
            dock.addView(item,new LinearLayout.LayoutParams(0,dp(66),1));
        }
        return dock;
    }

    private void mainAction(int i){
        switch(i){
            case 0: showPrintControl(); break;
            case 1: launchAny("com.google.android.apps.photos","com.miui.gallery"); break;
            case 2: showClients(); break;
            case 3: launchAny("com.google.android.documentsui","com.mi.android.globalFileexplorer"); break;
            case 4: showPrintControl(); break;
            case 5: showSocial(); break;
        }
    }

    private void enterSleepMode(){
        powerOn=false;
        expanded=false;
        saveUiState();

        rootFrame=new FrameLayout(this);
        rootFrame.setBackgroundColor(Color.BLACK);

        LinearLayout c=new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setGravity(Gravity.CENTER);
        c.setPadding(dp(24),dp(24),dp(24),dp(24));

        TextView logo=tv("WALLORA",tablet?42:34,Color.rgb(110,88,46));
        logo.setTypeface(Typeface.DEFAULT_BOLD);
        logo.setGravity(Gravity.CENTER);
        c.addView(logo);

        c.addView(space(26));

        TextView off=tv(tr("ПРИНТЕР ВИМКНЕНО","TISKÁRNA VYPNUTA","PRINTER OFF"),18,Color.rgb(140,115,65));
        off.setTypeface(Typeface.DEFAULT_BOLD);
        off.setGravity(Gravity.CENTER);
        c.addView(off);

        TextView h=tv(tr("Натисніть на екран, щоб активувати","Klepnutím aktivujete","Tap screen to wake"),12,Color.DKGRAY);
        h.setGravity(Gravity.CENTER);
        c.addView(h);

        rootFrame.addView(c,new FrameLayout.LayoutParams(-1,-1));
        rootFrame.setOnClickListener(v->{
            restoreBrightness();
            showPowerScreen();
        });
        installGestures(rootFrame);
        setContentView(rootFrame);
        dimScreen();
    }

    private void dimScreen(){
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.screenBrightness=0.02f;
        getWindow().setAttributes(lp);
    }

    private void restoreBrightness(){
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.screenBrightness=WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        getWindow().setAttributes(lp);
    }

    private void showLanguageDialog(){
        String[] labels={"Українська","Čeština","English"};
        new AlertDialog.Builder(this)
                .setTitle("Language / Jazyk / Мова")
                .setItems(labels,(d,which)->{
                    lang=which==0?"uk":which==1?"cz":"en";
                    prefs.edit().putString("lang",lang).apply();
                    if(powerOn) showDashboard(); else showPowerScreen();
                }).show();
    }

    private void wallParamsDialog(){
        LinearLayout l=new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(dp(20),dp(8),dp(20),0);
        EditText w=number(tr("Ширина, cm","Šířka, cm","Width, cm"));
        EditText h=number(tr("Висота, cm","Výška, cm","Height, cm"));
        EditText dist=number(tr("Відстань, mm","Vzdálenost, mm","Distance, mm"));
        w.setText(prefs.getString("wall_w","320"));
        h.setText(prefs.getString("wall_h","250"));
        dist.setText(prefs.getString("wall_dist","5"));
        for(EditText e:new EditText[]{w,h,dist}){ l.addView(e,new LinearLayout.LayoutParams(-1,dp(56))); l.addView(space(8)); }

        new AlertDialog.Builder(this)
                .setTitle(tr("Параметри стіни","Parametry stěny","Wall parameters"))
                .setView(l)
                .setNegativeButton(tr("Скасувати","Zrušit","Cancel"),null)
                .setPositiveButton(tr("Зберегти","Uložit","Save"),(d,x)->{
                    try{
                        double ww=num(w), hh=num(h);
                        prefs.edit()
                                .putString("wall_w",df.format(ww))
                                .putString("wall_h",df.format(hh))
                                .putString("wall_area",df.format(ww*hh/10000d))
                                .putString("wall_dist",dist.getText().toString())
                                .apply();
                        showDashboard();
                    }catch(Exception e){ toast(tr("Перевірте числа","Zkontrolujte čísla","Check numbers")); }
                }).show();
    }

    private void showPrintControl(){
        shell(tr("КЕРУВАННЯ ДРУКОМ","OVLÁDÁNÍ TISKU","PRINT CONTROL"),true);
        LinearLayout s=card();
        s.addView(label(tr("СТАТУС","STAV","STATUS")));
        TextView ready=tv("●  "+tr("ГОТОВО ДО РОБОТИ","PŘIPRAVENO","READY"),20,GREEN);
        ready.setTypeface(Typeface.DEFAULT_BOLD);
        s.addView(ready);
        s.addView(tv(tr(
                "BetterPrint / UltraPrint працюють на Windows-ПК. Тут запускається віддалене керування.",
                "BetterPrint / UltraPrint běží na Windows PC. Zde spustíte vzdálené ovládání.",
                "BetterPrint / UltraPrint run on the Windows PC. Remote control starts here."
        ),13,MUTED));
        addCard(s);

        Button any=button("AnyDesk");
        any.setOnClickListener(v->launchAny("com.anydesk.anydeskandroid"));
        addCard(any);

        Button team=button("TeamViewer");
        team.setOnClickListener(v->launchAny("com.teamviewer.teamviewer.market.mobile"));
        addCard(team);
    }

    private void showClients(){
        shell(tr("КЛІЄНТИ","KLIENTI","CLIENTS"),true);

        Button add=goldButton("＋ "+tr("Додати клієнта","Přidat klienta","Add client"));
        add.setOnClickListener(v->clientDialog());
        addCard(add);

        ArrayList<String> list=loadList("clients");
        if(list.isEmpty()){
            LinearLayout e=card();
            e.addView(tv(tr("Клієнтів поки немає.","Zatím žádní klienti.","No clients yet."),14,MUTED));
            addCard(e);
            return;
        }

        for(int i=list.size()-1;i>=0;i--){
            String[] p=parts(list.get(i),5);
            LinearLayout c=card();

            LinearLayout r=row();
            ImageView img=imageBox(88,88);
            setImageUri(img,p[4]);
            r.addView(img,new LinearLayout.LayoutParams(dp(88),dp(88)));

            LinearLayout text=new LinearLayout(this);
            text.setOrientation(LinearLayout.VERTICAL);
            text.setPadding(dp(12),0,0,0);
            TextView n=tv(p[0],18,GOLD_LIGHT); n.setTypeface(Typeface.DEFAULT_BOLD); text.addView(n);
            text.addView(tv(p[1],14,Color.WHITE));
            if(!p[2].isEmpty()) text.addView(tv(p[2],12,MUTED));
            if(!p[3].isEmpty()) text.addView(tv(p[3],12,MUTED));
            r.addView(text,new LinearLayout.LayoutParams(0,-2,1));

            c.addView(r);
            final String phone=p[1];
            if(!phone.isEmpty()) c.setOnClickListener(v->dial(phone));
            addCard(c);
        }
    }

    private void clientDialog(){
        pendingClientPhoto=null;
        LinearLayout l=new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(dp(20),dp(8),dp(20),0);

        EditText name=edit(tr("Ім’я / компанія","Jméno / firma","Name / company"));
        EditText phone=edit(tr("Телефон","Telefon","Phone"));
        phone.setInputType(InputType.TYPE_CLASS_PHONE);
        EditText city=edit(tr("Місто / адреса","Město / adresa","City / address"));
        EditText note=edit(tr("Що робимо / примітка","Co děláme / poznámka","Job / note"));

        clientPhotoPreview=imageBox(180,120);
        Button pick=button("📷 "+tr("Додати фото замовлення","Přidat fotku zakázky","Add order photo"));
        pick.setOnClickListener(v->pickImage(PICK_CLIENT_PHOTO));

        for(EditText e:new EditText[]{name,phone,city,note}){
            l.addView(e,new LinearLayout.LayoutParams(-1,dp(56)));
            l.addView(space(8));
        }
        l.addView(clientPhotoPreview,new LinearLayout.LayoutParams(-1,dp(120)));
        l.addView(space(8));
        l.addView(pick);

        new AlertDialog.Builder(this)
                .setTitle(tr("Новий клієнт","Nový klient","New client"))
                .setView(l)
                .setNegativeButton(tr("Скасувати","Zrušit","Cancel"),null)
                .setPositiveButton(tr("Зберегти","Uložit","Save"),(d,x)->{
                    if(name.getText().toString().trim().isEmpty()){
                        toast(tr("Вкажіть ім’я","Zadejte jméno","Enter a name"));
                        return;
                    }
                    ArrayList<String> a=loadList("clients");
                    a.add(clean(name.getText().toString())+"|"+
                            clean(phone.getText().toString())+"|"+
                            clean(city.getText().toString())+"|"+
                            clean(note.getText().toString())+"|"+
                            (pendingClientPhoto==null?"":pendingClientPhoto.toString()));
                    saveList("clients",a);
                    showClients();
                }).show();
    }

    private void showOrderDetails(int index){
        ArrayList<String> list=loadList("orders");
        if(index<0 || index>=list.size()){ showDashboard(); return; }
        String[] p=parts(list.get(index),13);

        shell(tr("ПОТОЧНЕ ЗАМОВЛЕННЯ","AKTUÁLNÍ ZAKÁZKA","CURRENT ORDER"),true);

        LinearLayout info=card();
        TextView n=tv(p[0],22,GOLD_LIGHT); n.setTypeface(Typeface.DEFAULT_BOLD); info.addView(n);
        info.addView(tv(p[2]+" × "+p[3]+" cm  •  "+p[4]+" m²",14,Color.WHITE));
        info.addView(tv(tr("Ціна: ","Cena: ","Price: ")+p[5]+" Kč",18,GOLD_LIGHT));
        info.addView(tv(tr("Статус: ","Stav: ","Status: ")+statusLabel(p[6]),14,Color.WHITE));
        if(!p[7].isEmpty()) info.addView(tv(tr("Примітка: ","Poznámka: ","Note: ")+p[7],13,MUTED));
        addCard(info);

        LinearLayout photos=card();
        photos.addView(label(tr("ФОТО ПРОЄКТУ","FOTKY PROJEKTU","PROJECT PHOTOS")));

        GridLayout g=new GridLayout(this);
        g.setColumnCount(tablet?3:1);
        addPhotoTile(g,tr("ІДЕЯ","NÁPAD","IDEA"),p[8]);
        addPhotoTile(g,tr("ДО","PŘED","BEFORE"),p[9]);
        addPhotoTile(g,tr("ПІСЛЯ","PO","AFTER"),p[10]);
        photos.addView(g);

        Button edit=goldButton("✎ "+tr("Редагувати замовлення","Upravit zakázku","Edit order"));
        final int idx=index;
        edit.setOnClickListener(v->orderDialog(p,idx));
        photos.addView(space(10));
        photos.addView(edit);

        addCard(photos);
    }

    private String statusLabel(String s){
        if("progress".equals(s)) return tr("Виконується","Probíhá","In progress");
        if("done".equals(s)) return tr("Завершено","Dokončeno","Completed");
        return tr("Активне","Aktivní","Active");
    }

    private void addPhotoTile(GridLayout g,String title,String uri){
        LinearLayout c=card();
        TextView t=label(title); t.setGravity(Gravity.CENTER); c.addView(t);
        ImageView img=imageBox(170,130);
        setImageUri(img,uri);
        c.addView(img,new LinearLayout.LayoutParams(-1,dp(130)));
        GridLayout.LayoutParams gp=new GridLayout.LayoutParams();
        gp.width=0; gp.height=-2;
        gp.columnSpec=GridLayout.spec(GridLayout.UNDEFINED,1,1f);
        gp.setMargins(dp(4),dp(4),dp(4),dp(4));
        g.addView(c,gp);
    }

    private void orderDialog(String[] existing, int index){
        pendingIdeaPhoto = existing!=null && !existing[8].isEmpty()?Uri.parse(existing[8]):null;
        pendingBeforePhoto = existing!=null && !existing[9].isEmpty()?Uri.parse(existing[9]):null;
        pendingAfterPhoto = existing!=null && !existing[10].isEmpty()?Uri.parse(existing[10]):null;

        ScrollView sv=new ScrollView(this);
        LinearLayout l=new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(dp(20),dp(8),dp(20),dp(10));
        sv.addView(l);

        EditText name=edit(tr("Назва проєкту / клієнт","Název projektu / klient","Project / client"));
        EditText w=number(tr("Ширина, cm","Šířka, cm","Width, cm"));
        EditText h=number(tr("Висота, cm","Výška, cm","Height, cm"));
        EditText price=number(tr("Ціна за замовлення, Kč","Cena zakázky, Kč","Order price, Kč"));
        EditText note=edit(tr("Примітка","Poznámka","Note"));

        if(existing!=null){
            name.setText(existing[0]); w.setText(existing[2]); h.setText(existing[3]); price.setText(existing[5]); note.setText(existing[7]);
        }

        for(EditText e:new EditText[]{name,w,h,price,note}){
            l.addView(e,new LinearLayout.LayoutParams(-1,dp(56)));
            l.addView(space(8));
        }

        ideaPreview=imageBox(180,110); setImageUri(ideaPreview, existing==null?"":existing[8]);
        beforePreview=imageBox(180,110); setImageUri(beforePreview, existing==null?"":existing[9]);
        afterPreview=imageBox(180,110); setImageUri(afterPreview, existing==null?"":existing[10]);

        addPickerRow(l,tr("Ідея / референс","Nápad / reference","Idea / reference"),ideaPreview,PICK_IDEA_PHOTO);
        addPickerRow(l,tr("Фото ДО","Foto PŘED","BEFORE photo"),beforePreview,PICK_BEFORE_PHOTO);
        addPickerRow(l,tr("Фото ПІСЛЯ","Foto PO","AFTER photo"),afterPreview,PICK_AFTER_PHOTO);

        String currentStatus=existing==null?"active":existing[6];
        RadioGroup statuses=new RadioGroup(this);
        statuses.setOrientation(RadioGroup.HORIZONTAL);
        String[] svv={"active","progress","done"};
        String[] sl={tr("Активне","Aktivní","Active"),tr("Виконується","Probíhá","In progress"),tr("Завершено","Dokončeno","Completed")};
        for(int i=0;i<3;i++){
            RadioButton rb=new RadioButton(this);
            rb.setId(1000+i);
            rb.setText(sl[i]);
            rb.setTextColor(Color.WHITE);
            rb.setButtonTintList(android.content.res.ColorStateList.valueOf(GOLD_LIGHT));
            if(svv[i].equals(currentStatus)) rb.setChecked(true);
            statuses.addView(rb,new RadioGroup.LayoutParams(0,dp(52),1));
        }
        l.addView(statuses);

        new AlertDialog.Builder(this)
                .setTitle(existing==null?tr("Нове замовлення","Nová zakázka","New order"):tr("Редагувати замовлення","Upravit zakázku","Edit order"))
                .setView(sv)
                .setNegativeButton(tr("Скасувати","Zrušit","Cancel"),null)
                .setPositiveButton(tr("Зберегти","Uložit","Save"),(d,x)->{
                    try{
                        double ww=num(w),hh=num(h);
                        double area=ww*hh/10000d;
                        String stat="active";
                        int checked=statuses.getCheckedRadioButtonId();
                        if(checked==1001) stat="progress";
                        if(checked==1002) stat="done";

                        String[] p=new String[13];
                        p[0]=clean(name.getText().toString());
                        p[1]=new SimpleDateFormat("dd.MM.yyyy",Locale.getDefault()).format(new Date());
                        p[2]=df.format(ww);
                        p[3]=df.format(hh);
                        p[4]=df.format(area);
                        p[5]=clean(price.getText().toString());
                        p[6]=stat;
                        p[7]=clean(note.getText().toString());
                        p[8]=pendingIdeaPhoto==null?"":pendingIdeaPhoto.toString();
                        p[9]=pendingBeforePhoto==null?"":pendingBeforePhoto.toString();
                        p[10]=pendingAfterPhoto==null?"":pendingAfterPhoto.toString();
                        p[11]="";
                        p[12]="";

                        ArrayList<String> list=loadList("orders");
                        if(index>=0 && index<list.size()) list.set(index,join(p));
                        else list.add(join(p));
                        saveList("orders",list);

                        prefs.edit()
                                .putString("wall_w",p[2])
                                .putString("wall_h",p[3])
                                .putString("wall_area",p[4])
                                .apply();

                        showDashboard();
                    }catch(Exception e){ toast(tr("Перевірте ширину й висоту","Zkontrolujte šířku a výšku","Check width and height")); }
                }).show();
    }

    private void addPickerRow(LinearLayout parent,String title,ImageView preview,int request){
        LinearLayout c=card();
        c.addView(label(title));
        c.addView(preview,new LinearLayout.LayoutParams(-1,dp(110)));
        Button b=button("📷 "+tr("Вибрати фото","Vybrat fotku","Choose photo"));
        b.setOnClickListener(v->pickImage(request));
        c.addView(space(6));
        c.addView(b);
        parent.addView(c,new LinearLayout.LayoutParams(-1,-2));
        parent.addView(space(8));
    }

    private ImageView imageBox(int w,int h){
        ImageView img=new ImageView(this);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img.setBackground(bg(Color.rgb(20,20,20),12,Color.rgb(80,62,32),1));
        img.setImageDrawable(null);
        return img;
    }

    private void setImageUri(ImageView img,String uri){
        if(img==null) return;
        if(uri==null || uri.trim().isEmpty()){
            img.setImageDrawable(null);
            img.setBackground(bg(Color.rgb(20,20,20),12,Color.rgb(80,62,32),1));
            return;
        }
        try{
            img.setImageURI(Uri.parse(uri));
        }catch(Exception e){
            img.setImageDrawable(null);
        }
    }

    private void pickImage(int requestCode){
        Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(i,requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode!=RESULT_OK || data==null || data.getData()==null) return;

        Uri uri=data.getData();
        try{
            getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }catch(Exception ignored){}

        if(requestCode==PICK_CLIENT_PHOTO){
            pendingClientPhoto=uri;
            if(clientPhotoPreview!=null) clientPhotoPreview.setImageURI(uri);
        }else if(requestCode==PICK_IDEA_PHOTO){
            pendingIdeaPhoto=uri;
            if(ideaPreview!=null) ideaPreview.setImageURI(uri);
        }else if(requestCode==PICK_BEFORE_PHOTO){
            pendingBeforePhoto=uri;
            if(beforePreview!=null) beforePreview.setImageURI(uri);
        }else if(requestCode==PICK_AFTER_PHOTO){
            pendingAfterPhoto=uri;
            if(afterPreview!=null) afterPreview.setImageURI(uri);
        }
    }

    private ArrayList<String> loadList(String key){
        String raw=prefs.getString(key,"");
        ArrayList<String> out=new ArrayList<>();
        if(raw.isEmpty()) return out;
        for(String x:raw.split("\\n---WALLORA---\\n",-1))
            if(!x.trim().isEmpty()) out.add(x);
        return out;
    }

    private void saveList(String key,ArrayList<String> list){
        StringBuilder b=new StringBuilder();
        for(int i=0;i<list.size();i++){
            if(i>0)b.append("\n---WALLORA---\n");
            b.append(list.get(i));
        }
        prefs.edit().putString(key,b.toString()).apply();
    }

    private String[] parts(String s,int count){
        String[] a=s.split("\\|",-1);
        String[] o=new String[count];
        for(int i=0;i<count;i++) o[i]=i<a.length?a[i]:"";
        return o;
    }

    private String join(String[] a){
        StringBuilder b=new StringBuilder();
        for(int i=0;i<a.length;i++){
            if(i>0)b.append("|");
            b.append(a[i]==null?"":a[i]);
        }
        return b.toString();
    }

    private String clean(String s){
        return s.replace("|","/").replace("\n"," ").trim();
    }

    private double num(EditText e){
        return Double.parseDouble(e.getText().toString().trim().replace(',','.'));
    }

    private void showSocial(){
        shell(tr("СОЦМЕРЕЖІ","SOCIÁLNÍ SÍTĚ","SOCIAL"),true);
        String[][] apps={
                {"Instagram","com.instagram.android"},
                {"TikTok","com.zhiliaoapp.musically"},
                {"Threads","com.instagram.barcelona"},
                {"Photos","com.google.android.apps.photos"},
                {"Canva","com.canva.editor"}
        };
        for(String[] a:apps){
            Button b=button(a[0]);
            b.setOnClickListener(v->launchAny(a[1]));
            addCard(b);
        }
    }

    private void showSettings(){
        shell(tr("НАЛАШТУВАННЯ","NASTAVENÍ","SETTINGS"),true);

        Button language=button("🌐 "+tr("Мова","Jazyk","Language"));
        language.setOnClickListener(v->showLanguageDialog());
        addCard(language);

        Button sys=button(tr("Відкрити налаштування Android","Otevřít nastavení Android","Open Android settings"));
        sys.setOnClickListener(v->startActivity(new Intent(Settings.ACTION_SETTINGS)));
        addCard(sys);

        Button home=button(tr("Вибір домашнього екрана / Launcher","Výběr domovské obrazovky / Launcher","Choose home app / Launcher"));
        home.setOnClickListener(v->{
            try{ startActivity(new Intent(Settings.ACTION_HOME_SETTINGS)); }
            catch(Exception e){ startActivity(new Intent(Settings.ACTION_SETTINGS)); }
        });
        addCard(home);

        Button clear=button(tr("Очистити локальні дані WALLORA","Vymazat lokální data WALLORA","Clear WALLORA local data"));
        clear.setOnClickListener(v->new AlertDialog.Builder(this)
                .setTitle(tr("Очистити дані?","Vymazat data?","Clear data?"))
                .setMessage(tr("Будуть видалені клієнти, замовлення та налаштування.","Budou smazáni klienti, zakázky a nastavení.","Clients, orders and settings will be deleted."))
                .setNegativeButton(tr("Скасувати","Zrušit","Cancel"),null)
                .setPositiveButton(tr("Видалити","Smazat","Delete"),(d,x)->{
                    prefs.edit().clear().apply();
                    lang="uk"; powerOn=false; expanded=false;
                    showPowerScreen();
                }).show());
        addCard(clear);

        LinearLayout about=card();
        about.addView(label(tr("ВЕРСІЯ","VERZE","VERSION")));
        about.addView(tv("WALLORA Control 6.0\nUA / CZ / EN\nPower Mode • Gestures • Clients + photos • Orders + idea/before/after",13,Color.WHITE));
        addCard(about);
    }

    private void dial(String p){
        try{ startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+Uri.encode(p)))); }
        catch(Exception e){ toast(tr("Не вдалося відкрити телефон","Telefon se nepodařilo otevřít","Could not open phone")); }
    }

    private void launchAny(String... packages){
        PackageManager pm=getPackageManager();
        for(String p:packages){
            try{
                Intent i=pm.getLaunchIntentForPackage(p);
                if(i!=null){ startActivity(i); return; }
            }catch(Exception ignored){}
        }
        toast(tr("Застосунок не встановлено","Aplikace není nainstalována","App is not installed"));
    }

    private void toast(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }
}
