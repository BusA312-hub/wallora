package com.wallora.home;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
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
    private static final int GOLD = Color.rgb(220, 171, 72);
    private static final int GOLD_LIGHT = Color.rgb(255, 220, 150);
    private static final int WHITE = Color.rgb(245,245,245);
    private static final int MUTED = Color.rgb(188,188,188);
    private static final int GLASS = Color.argb(214, 12,12,12);
    private static final int GLASS_2 = Color.argb(228, 20,20,20);
    private static final int LINE = Color.argb(170, 111,83,37);
    private static final int GREEN = Color.rgb(109, 222, 126);

    private final DecimalFormat df = new DecimalFormat("0.##");
    private SharedPreferences prefs;
    private boolean tablet;
    private LinearLayout page;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("wallora", MODE_PRIVATE);
        tablet = getResources().getConfiguration().smallestScreenWidthDp >= 600;
        immersive();
        showDashboard();
    }

    @Override public void onResume(){ super.onResume(); immersive(); }

    private void immersive(){
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.BLACK);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private int dp(int n){ return (int)(n*getResources().getDisplayMetrics().density+0.5f); }

    private GradientDrawable rounded(int color, int radius, int strokeColor, int stroke){
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(dp(radius));
        if(stroke>0) g.setStroke(dp(stroke), strokeColor);
        return g;
    }

    private TextView tv(String s, int sp, int color){
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(sp);
        t.setTextColor(color);
        t.setIncludeFontPadding(false);
        return t;
    }

    private TextView brandText(String s, int sp){
        TextView t = tv(s, sp, GOLD_LIGHT);
        t.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        if(Build.VERSION.SDK_INT >= 21) t.setLetterSpacing(.12f);
        return t;
    }

    private Space space(int h){ Space s=new Space(this); s.setLayoutParams(new LinearLayout.LayoutParams(1,dp(h))); return s; }

    private LinearLayout glassCard(int radius){
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(dp(16),dp(14),dp(16),dp(14));
        c.setBackground(rounded(GLASS, radius, LINE, 1));
        return c;
    }

    private Button goldButton(String text){
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextColor(Color.BLACK);
        b.setTextSize(14);
        b.setTypeface(Typeface.DEFAULT_BOLD);
        b.setGravity(Gravity.CENTER);
        b.setBackground(rounded(GOLD_LIGHT, 14, GOLD_LIGHT, 1));
        return b;
    }

    private Button darkButton(String text){
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextColor(WHITE);
        b.setTextSize(14);
        b.setGravity(Gravity.CENTER);
        b.setBackground(rounded(GLASS_2, 14, LINE, 1));
        return b;
    }

    private EditText edit(String hint){
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setHintTextColor(Color.rgb(135,135,135));
        e.setTextColor(WHITE);
        e.setTextSize(14);
        e.setSingleLine(true);
        e.setPadding(dp(14),dp(10),dp(14),dp(10));
        e.setBackground(rounded(GLASS_2, 12, Color.rgb(67,55,35), 1));
        return e;
    }

    private EditText number(String hint){
        EditText e = edit(hint);
        e.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        return e;
    }

    private FrameLayout backgroundFrame(){
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundResource(com.wallora.home.R.drawable.wallora_bg);
        View shade = new View(this);
        shade.setBackgroundColor(Color.argb(tablet?70:145,0,0,0));
        root.addView(shade,new FrameLayout.LayoutParams(-1,-1));
        return root;
    }

    private void showDashboard(){
        tablet = getResources().getConfiguration().smallestScreenWidthDp >= 600;
        FrameLayout root = backgroundFrame();
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(tablet?28:16),dp(tablet?22:16),dp(tablet?28:16),dp(24));
        scroll.addView(content,new ScrollView.LayoutParams(-1,-2));
        root.addView(scroll,new FrameLayout.LayoutParams(-1,-1));
        setContentView(root);

        // top brand
        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView time = tv(new SimpleDateFormat("HH:mm\nEEE, d MMM", Locale.getDefault()).format(new Date()), tablet?16:12, WHITE);
        time.setGravity(Gravity.LEFT);
        top.addView(time,new LinearLayout.LayoutParams(0,dp(tablet?64:50),1));
        LinearLayout brandBox = new LinearLayout(this);
        brandBox.setOrientation(LinearLayout.VERTICAL); brandBox.setGravity(Gravity.CENTER);
        TextView logo = brandText("WALLORA", tablet?42:26); logo.setGravity(Gravity.CENTER);
        TextView slogan = tv("PRINT YOUR WORLD", tablet?13:9, GOLD_LIGHT); slogan.setGravity(Gravity.CENTER);
        if(Build.VERSION.SDK_INT>=21) slogan.setLetterSpacing(.28f);
        brandBox.addView(logo); brandBox.addView(slogan);
        top.addView(brandBox,new LinearLayout.LayoutParams(0,dp(tablet?82:60),2));
        TextView status = tv("● ONLINE",tablet?13:10,GREEN); status.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        top.addView(status,new LinearLayout.LayoutParams(0,dp(tablet?64:50),1));
        content.addView(top);
        content.addView(space(tablet?16:10));

        if(tablet){
            LinearLayout infoRow = new LinearLayout(this);
            infoRow.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout current = currentOrderCard();
            LinearLayout wall = wallParamsCard();
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(0,dp(190),1); cp.setMargins(0,0,dp(8),0);
            LinearLayout.LayoutParams wp = new LinearLayout.LayoutParams(0,dp(190),1); wp.setMargins(dp(8),0,0,0);
            infoRow.addView(current,cp); infoRow.addView(wall,wp);
            content.addView(infoRow);
        } else {
            content.addView(currentOrderCard(),new LinearLayout.LayoutParams(-1,-2));
            content.addView(space(10));
            content.addView(wallParamsCard(),new LinearLayout.LayoutParams(-1,-2));
        }

        content.addView(space(tablet?18:12));

        String[][] main = {
            {"▣","PRINT","Новый проект"},
            {"▧","GALLERY","Галерея работ"},
            {"◉","CLIENTS","Клиенты"},
            {"▰","FILES","Файлы"},
            {"▣","REMOTE PC","Управление ПК"},
            {"◎","SOCIAL","Соцсети"}
        };
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(tablet?6:2);
        for(int i=0;i<main.length;i++){
            final int ix=i;
            LinearLayout tile = premiumTile(main[i][0],main[i][1],main[i][2], i==0);
            tile.setOnClickListener(v->mainAction(ix));
            GridLayout.LayoutParams gp = new GridLayout.LayoutParams();
            gp.width=0; gp.height=dp(tablet?132:118);
            gp.columnSpec=GridLayout.spec(GridLayout.UNDEFINED,1,1f);
            gp.setMargins(dp(5),dp(5),dp(5),dp(5));
            grid.addView(tile,gp);
        }
        content.addView(grid,new LinearLayout.LayoutParams(-1,-2));
        content.addView(space(tablet?16:10));

        LinearLayout dock = new LinearLayout(this);
        dock.setOrientation(LinearLayout.HORIZONTAL);
        dock.setGravity(Gravity.CENTER);
        dock.setPadding(dp(10),dp(9),dp(10),dp(9));
        dock.setBackground(rounded(Color.argb(225,8,8,8),18,LINE,1));
        String[][] apps={
            {"B","BetterPrint","remote"},{"U","UltraPrint","remote"},{"A","AnyDesk","com.anydesk.anydeskandroid"},
            {"T","TeamViewer","com.teamviewer.teamviewer.market.mobile"},{"D","Drive","com.google.android.apps.docs"},
            {"P","Photos","com.google.android.apps.photos"},{"C","Chrome","com.android.chrome"},{"⚙","Settings","settings"}
        };
        int max = tablet?apps.length:5;
        for(int i=0;i<max;i++){
            final String action=apps[i][2];
            LinearLayout d = dockItem(apps[i][0],apps[i][1]);
            d.setOnClickListener(v->{
                if("remote".equals(action)) showPrintControl();
                else if("settings".equals(action)) showSettings();
                else launchAny(action);
            });
            dock.addView(d,new LinearLayout.LayoutParams(0,dp(tablet?70:60),1));
        }
        content.addView(dock,new LinearLayout.LayoutParams(-1,-2));
    }

    private LinearLayout currentOrderCard(){
        LinearLayout c = glassCard(18);
        TextView h = brandText("ТЕКУЩИЙ ЗАКАЗ", tablet?15:13); c.addView(h);
        c.addView(space(8));
        ArrayList<String> orders = loadList("orders");
        if(orders.isEmpty()){
            c.addView(tv("Нет активного заказа",tablet?18:16,WHITE));
            c.addView(space(6));
            c.addView(tv("Создай заказ — здесь появятся проект, размер и статус.",12,MUTED));
        } else {
            String[] p=parts(orders.get(orders.size()-1),7);
            TextView n=tv(p[0].isEmpty()?"Проект":p[0],tablet?22:18,WHITE); n.setTypeface(Typeface.DEFAULT_BOLD); c.addView(n);
            c.addView(space(5));
            c.addView(tv((p[2].isEmpty()?"—":p[2])+" × "+(p[3].isEmpty()?"—":p[3])+" cm",14,WHITE));
            c.addView(tv("Площадь: "+(p[4].isEmpty()?"—":p[4])+" m²",13,MUTED));
            c.addView(tv("Статус: "+(p[6].isEmpty()?"Подготовка":p[6]),13,GOLD_LIGHT));
        }
        c.setOnClickListener(v->showOrders());
        return c;
    }

    private LinearLayout wallParamsCard(){
        LinearLayout c=glassCard(18);
        TextView h=brandText("ПАРАМЕТРЫ СТЕНЫ",tablet?15:13); c.addView(h); c.addView(space(8));
        String[][] rows={{"Ширина","320 cm"},{"Высота","250 cm"},{"Площадь","8.0 m²"},{"Расстояние","5 mm"}};
        for(String[] r:rows){
            LinearLayout line=new LinearLayout(this); line.setOrientation(LinearLayout.HORIZONTAL);
            TextView l=tv(r[0],13,MUTED); TextView v=tv(r[1],13,WHITE); v.setTypeface(Typeface.DEFAULT_BOLD); v.setGravity(Gravity.RIGHT);
            line.addView(l,new LinearLayout.LayoutParams(0,dp(28),1)); line.addView(v,new LinearLayout.LayoutParams(0,dp(28),1)); c.addView(line);
        }
        c.setOnClickListener(v->showCalculator());
        return c;
    }

    private LinearLayout premiumTile(String icon,String title,String sub,boolean accent){
        LinearLayout c=new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL); c.setGravity(Gravity.CENTER); c.setPadding(dp(8),dp(10),dp(8),dp(8));
        c.setBackground(rounded(accent?Color.argb(230,45,31,8):Color.argb(220,10,10,10),18,accent?GOLD_LIGHT:LINE,accent?2:1));
        TextView i=tv(icon,tablet?32:27,GOLD_LIGHT); i.setGravity(Gravity.CENTER); c.addView(i);
        c.addView(space(6));
        TextView t=tv(title,tablet?15:14,accent?GOLD_LIGHT:WHITE); t.setTypeface(Typeface.DEFAULT_BOLD); t.setGravity(Gravity.CENTER); c.addView(t);
        c.addView(space(4));
        TextView s=tv(sub,11,accent?GOLD:MUTED); s.setGravity(Gravity.CENTER); c.addView(s);
        return c;
    }

    private LinearLayout dockItem(String icon,String label){
        LinearLayout c=new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setGravity(Gravity.CENTER);
        TextView i=tv(icon,tablet?20:18,GOLD_LIGHT); i.setTypeface(Typeface.DEFAULT_BOLD); i.setGravity(Gravity.CENTER); c.addView(i);
        TextView l=tv(label,tablet?9:8,MUTED); l.setGravity(Gravity.CENTER); c.addView(l);
        return c;
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

    // -------- internal pages --------
    private void openPage(String name){
        tablet = getResources().getConfiguration().smallestScreenWidthDp >= 600;
        FrameLayout root=backgroundFrame();
        ScrollView sv=new ScrollView(this); sv.setFillViewport(true);
        LinearLayout holder=new LinearLayout(this); holder.setOrientation(LinearLayout.VERTICAL); holder.setPadding(dp(tablet?26:14),dp(16),dp(tablet?26:14),dp(26));
        LinearLayout bar=new LinearLayout(this); bar.setOrientation(LinearLayout.HORIZONTAL); bar.setGravity(Gravity.CENTER_VERTICAL);
        Button back=darkButton("‹ WALLORA"); back.setOnClickListener(v->showDashboard()); bar.addView(back,new LinearLayout.LayoutParams(dp(tablet?150:116),dp(48)));
        TextView title=brandText(name,tablet?26:20); title.setGravity(Gravity.CENTER_VERTICAL); LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(0,dp(50),1); tp.setMargins(dp(14),0,0,0); bar.addView(title,tp);
        holder.addView(bar); holder.addView(space(12));
        page=new LinearLayout(this); page.setOrientation(LinearLayout.VERTICAL); holder.addView(page,new LinearLayout.LayoutParams(-1,-2));
        sv.addView(holder); root.addView(sv,new FrameLayout.LayoutParams(-1,-1)); setContentView(root);
    }

    private void addPageCard(View v){ LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2); lp.setMargins(0,0,0,dp(12)); page.addView(v,lp); }

    private void showPrintControl(){
        openPage("PRINT CONTROL");
        LinearLayout s=glassCard(18); s.addView(brandText("● ГОТОВ К РАБОТЕ",18)); s.addView(space(8)); s.addView(tv("BetterPrint и UltraPrint работают на Windows-ПК. Отсюда запускается удалённое управление.",13,MUTED)); addPageCard(s);
        LinearLayout r=glassCard(18); r.addView(tv("Удалённое управление",16,WHITE)); r.addView(space(8)); Button a=goldButton("Открыть AnyDesk"); a.setOnClickListener(v->launchAny("com.anydesk.anydeskandroid")); r.addView(a,new LinearLayout.LayoutParams(-1,dp(52))); r.addView(space(8)); Button t=darkButton("Открыть TeamViewer"); t.setOnClickListener(v->launchAny("com.teamviewer.teamviewer.market.mobile")); r.addView(t,new LinearLayout.LayoutParams(-1,dp(52))); addPageCard(r);
        LinearLayout q=glassCard(18); q.addView(tv("БЫСТРЫЕ ДЕЙСТВИЯ",13,GOLD_LIGHT)); String[] x={"Nozzle test","Очистка","Парковка","Калибровка","AUTO distance 5 mm"}; for(String z:x){ TextView e=tv("• "+z,14,WHITE); e.setPadding(0,dp(5),0,dp(5)); q.addView(e);} addPageCard(q);
        Button tests=goldButton("Открыть чек-лист перед печатью"); tests.setOnClickListener(v->showTests()); addPageCard(tests);
    }

    private ArrayList<String> loadList(String key){ String raw=prefs.getString(key,""); ArrayList<String> out=new ArrayList<>(); if(raw.isEmpty())return out; for(String x:raw.split("\\n---WALLORA---\\n",-1)) if(!x.trim().isEmpty()) out.add(x); return out; }
    private void saveList(String key,ArrayList<String> list){ StringBuilder b=new StringBuilder(); for(int i=0;i<list.size();i++){ if(i>0)b.append("\n---WALLORA---\n"); b.append(list.get(i)); } prefs.edit().putString(key,b.toString()).apply(); }
    private String[] parts(String s,int count){ String[] a=s.split("\\|",-1); String[] o=new String[count]; for(int i=0;i<count;i++)o[i]=i<a.length?a[i]:""; return o; }
    private String clean(String s){ return s.replace("|","/").replace("\n"," ").trim(); }

    private void showOrders(){
        openPage("ORDERS"); Button add=goldButton("＋ Новый заказ"); add.setOnClickListener(v->orderDialog()); addPageCard(add);
        ArrayList<String> list=loadList("orders"); if(list.isEmpty()){ LinearLayout e=glassCard(18); e.addView(tv("Заказов пока нет.",15,MUTED)); addPageCard(e); return; }
        Collections.reverse(list); for(String rec:list){ String[] p=parts(rec,7); LinearLayout c=glassCard(18); TextView n=brandText(p[0],18); c.addView(n); c.addView(tv(p[1]+"  •  "+p[2]+" × "+p[3]+" cm  •  "+p[4]+" m²",13,WHITE)); c.addView(tv("Цена: "+p[5]+" Kč  •  "+p[6],12,MUTED)); addPageCard(c); }
    }

    private void orderDialog(){
        LinearLayout l=new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); l.setPadding(dp(18),dp(6),dp(18),0);
        EditText name=edit("Проект / клиент"),w=number("Ширина, cm"),h=number("Высота, cm"),rate=number("Цена за m², Kč"),state=edit("Статус");
        for(EditText e:new EditText[]{name,w,h,rate,state}){ l.addView(e,new LinearLayout.LayoutParams(-1,dp(56))); l.addView(space(8)); }
        new AlertDialog.Builder(this).setTitle("Новый заказ").setView(l).setNegativeButton("Отмена",null).setPositiveButton("Сохранить",(d,x)->{ try{ double ww=num(w),hh=num(h),rr=rate.getText().toString().trim().isEmpty()?0:num(rate),area=ww*hh/10000d,total=area*rr; ArrayList<String>a=loadList("orders"); a.add(clean(name.getText().toString())+"|"+new SimpleDateFormat("dd.MM.yyyy",Locale.getDefault()).format(new Date())+"|"+df.format(ww)+"|"+df.format(hh)+"|"+df.format(area)+"|"+df.format(total)+"|"+clean(state.getText().toString())); saveList("orders",a); showOrders(); }catch(Exception e){ toast("Проверь размеры"); }}).show();
    }

    private void showClients(){
        openPage("CLIENTS"); Button add=goldButton("＋ Добавить клиента"); add.setOnClickListener(v->clientDialog()); addPageCard(add);
        ArrayList<String> list=loadList("clients"); if(list.isEmpty()){ LinearLayout e=glassCard(18); e.addView(tv("Клиентская база пустая.",15,MUTED)); addPageCard(e); return; }
        Collections.reverse(list); for(String rec:list){ String[] p=parts(rec,4); LinearLayout c=glassCard(18); c.addView(brandText(p[0],18)); c.addView(tv(p[1]+(p[2].isEmpty()?"":"  •  "+p[2]),13,WHITE)); if(!p[3].isEmpty())c.addView(tv(p[3],12,MUTED)); if(!p[1].isEmpty())c.setOnClickListener(v->dial(p[1])); addPageCard(c); }
    }

    private void clientDialog(){
        LinearLayout l=new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); l.setPadding(dp(18),dp(6),dp(18),0);
        EditText name=edit("Имя / компания"),phone=edit("Телефон"),city=edit("Город / адрес"),note=edit("Комментарий"); phone.setInputType(InputType.TYPE_CLASS_PHONE);
        for(EditText e:new EditText[]{name,phone,city,note}){ l.addView(e,new LinearLayout.LayoutParams(-1,dp(56))); l.addView(space(8)); }
        new AlertDialog.Builder(this).setTitle("Новый клиент").setView(l).setNegativeButton("Отмена",null).setPositiveButton("Сохранить",(d,x)->{ if(name.getText().toString().trim().isEmpty()){toast("Укажи имя клиента");return;} ArrayList<String>a=loadList("clients"); a.add(clean(name.getText().toString())+"|"+clean(phone.getText().toString())+"|"+clean(city.getText().toString())+"|"+clean(note.getText().toString())); saveList("clients",a); showClients(); }).show();
    }

    private void showCalculator(){
        openPage("CALCULATOR"); LinearLayout c=glassCard(18); EditText w=number("Ширина стены, cm"),h=number("Высота стены, cm"),rate=number("Цена за m², Kč"); TextView r=brandText("Введите размеры",20); Button go=goldButton("Рассчитать"); for(EditText e:new EditText[]{w,h,rate}){c.addView(e,new LinearLayout.LayoutParams(-1,dp(56)));c.addView(space(8));} c.addView(go,new LinearLayout.LayoutParams(-1,dp(52))); c.addView(space(14)); c.addView(r); addPageCard(c); go.setOnClickListener(v->{try{double ww=num(w),hh=num(h),rr=rate.getText().toString().trim().isEmpty()?0:num(rate),area=ww*hh/10000d; r.setText(df.format(area)+" m²"+(rr>0?"  •  "+df.format(area*rr)+" Kč":"")); prefs.edit().putString("last_rate",rate.getText().toString()).apply();}catch(Exception e){toast("Проверь числа");}}); rate.setText(prefs.getString("last_rate",""));
    }

    private void showTests(){
        openPage("PRINTER TESTS"); String[] tests={"Проверить чернила и воздух в магистралях","Сделать Nozzle Test","Проверить Vertical / Horizontal Correct","Проверить ровность стены","AUTO distance: стабильный зазор","Сделать цветовой тест 10×10 cm","Проверить Mirror / ориентацию","Зафиксировать точку старта","Проверить питание и кабели","Запускать основной рисунок"}; for(String x:tests){ LinearLayout c=glassCard(16); CheckBox cb=new CheckBox(this); cb.setText(x); cb.setTextColor(WHITE); cb.setTextSize(14); cb.setButtonTintList(ColorStateList.valueOf(GOLD_LIGHT)); c.addView(cb); addPageCard(c);} }

    private void showSocial(){
        openPage("SOCIAL"); String[][] apps={{"Instagram","com.instagram.android"},{"TikTok","com.zhiliaoapp.musically"},{"Threads","com.instagram.barcelona"},{"Photos","com.google.android.apps.photos"},{"Canva","com.canva.editor"}}; for(String[] a:apps){ Button b=darkButton(a[0]); b.setOnClickListener(v->launchAny(a[1])); addPageCard(b);} }

    private void showSettings(){
        openPage("SETTINGS"); Button sys=darkButton("Открыть настройки Android"); sys.setOnClickListener(v->startActivity(new Intent(Settings.ACTION_SETTINGS))); addPageCard(sys); Button home=darkButton("Выбор домашнего экрана"); home.setOnClickListener(v->{try{startActivity(new Intent(Settings.ACTION_HOME_SETTINGS));}catch(Exception e){startActivity(new Intent(Settings.ACTION_SETTINGS));}}); addPageCard(home); LinearLayout a=glassCard(18); a.addView(brandText("WALLORA CONTROL 4.0",18)); a.addView(tv("Premium launcher для Redmi Pad 2 и Xiaomi 17T",13,MUTED)); addPageCard(a);
    }

    private double num(EditText e){ return Double.parseDouble(e.getText().toString().trim().replace(',','.')); }
    private void dial(String p){ try{startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(p))));}catch(Exception e){toast("Не удалось открыть телефон");} }
    private void launchAny(String... packages){ PackageManager pm=getPackageManager(); for(String p:packages){ try{ Intent i=pm.getLaunchIntentForPackage(p); if(i!=null){ startActivity(i); return; }}catch(Exception ignored){} } toast("Приложение не установлено"); }
    private void toast(String s){ Toast.makeText(this,s,Toast.LENGTH_SHORT).show(); }
}
