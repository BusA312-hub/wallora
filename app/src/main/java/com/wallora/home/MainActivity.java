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
    private static final int BG = Color.rgb(8,8,8);
    private static final int PANEL = Color.rgb(22,22,22);
    private static final int PANEL_2 = Color.rgb(30,30,30);
    private static final int MUTED = Color.rgb(170,170,170);
    private final DecimalFormat df = new DecimalFormat("0.##");
    private android.content.SharedPreferences prefs;
    private LinearLayout page;
    private TextView title;
    private boolean tablet;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("wallora", MODE_PRIVATE);
        tablet = getResources().getConfiguration().smallestScreenWidthDp >= 600;
        immersive();
        showDashboard();
    }

    @Override public void onResume() { super.onResume(); immersive(); }
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

    private int dp(int n){ return (int)(n*getResources().getDisplayMetrics().density+0.5f); }
    private GradientDrawable bg(int color, int radius, int strokeColor, int stroke){
        GradientDrawable g=new GradientDrawable(); g.setColor(color); g.setCornerRadius(dp(radius));
        if(stroke>0) g.setStroke(dp(stroke), strokeColor); return g;
    }
    private TextView tv(String s,int sp,int color){ TextView t=new TextView(this); t.setText(s); t.setTextSize(sp); t.setTextColor(color); t.setPadding(dp(2),dp(2),dp(2),dp(2)); return t; }
    private TextView label(String s){ TextView t=tv(s,12,MUTED); t.setAllCaps(true); t.setLetterSpacing(.12f); return t; }
    private Space space(int h){ Space s=new Space(this); s.setLayoutParams(new LinearLayout.LayoutParams(1,dp(h))); return s; }
    private Button button(String text){
        Button b=new Button(this); b.setText(text); b.setTextColor(Color.WHITE); b.setTextSize(14); b.setAllCaps(false); b.setGravity(Gravity.CENTER);
        b.setBackground(bg(PANEL_2,14,GOLD,1)); b.setPadding(dp(12),dp(10),dp(12),dp(10)); return b;
    }
    private EditText edit(String hint){ EditText e=new EditText(this); e.setHint(hint); e.setHintTextColor(Color.rgb(110,110,110)); e.setTextColor(Color.WHITE); e.setSingleLine(true); e.setPadding(dp(14),dp(12),dp(14),dp(12)); e.setBackground(bg(PANEL_2,12,Color.rgb(70,70,70),1)); return e; }
    private EditText number(String hint){ EditText e=edit(hint); e.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL); return e; }

    private ScrollView shell(String name, boolean homeButton){
        tablet = getResources().getConfiguration().smallestScreenWidthDp >= 600;
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(BG); root.setPadding(dp(tablet?28:16),dp(18),dp(tablet?28:16),dp(28));
        LinearLayout bar=new LinearLayout(this); bar.setGravity(Gravity.CENTER_VERTICAL); bar.setOrientation(LinearLayout.HORIZONTAL);
        if(homeButton){ Button back=button("‹  WALLORA"); back.setOnClickListener(v->showDashboard()); bar.addView(back,new LinearLayout.LayoutParams(tablet?dp(170):dp(126),dp(52))); }
        title=tv(name, tablet?28:22, GOLD_LIGHT); title.setTypeface(Typeface.DEFAULT_BOLD); title.setGravity(Gravity.CENTER_VERTICAL); LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(0,dp(60),1); tp.setMargins(dp(16),0,0,0); bar.addView(title,tp);
        TextView brand=tv("PRINT YOUR WORLD", tablet?12:10,GOLD); brand.setLetterSpacing(.12f); brand.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT); bar.addView(brand,new LinearLayout.LayoutParams(tablet?dp(220):0,dp(60)));
        root.addView(bar);
        root.addView(space(12));
        page=new LinearLayout(this); page.setOrientation(LinearLayout.VERTICAL);
        root.addView(page,new LinearLayout.LayoutParams(-1,-2));
        ScrollView sv=new ScrollView(this); sv.setFillViewport(true); sv.addView(root); setContentView(sv); return sv;
    }

    private LinearLayout card(){ LinearLayout c=new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setPadding(dp(16),dp(16),dp(16),dp(16)); c.setBackground(bg(PANEL,16,Color.rgb(66,52,28),1)); return c; }
    private void addCard(View v){ LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2); lp.setMargins(0,0,0,dp(12)); page.addView(v,lp); }
    private LinearLayout row(){ LinearLayout r=new LinearLayout(this); r.setOrientation(LinearLayout.HORIZONTAL); r.setGravity(Gravity.CENTER_VERTICAL); return r; }

    private void showDashboard(){
        shell("WALLORA CONTROL",false);
        LinearLayout hero=card();
        TextView logo=tv("WALLORA",tablet?44:34,GOLD_LIGHT); logo.setTypeface(Typeface.DEFAULT_BOLD); logo.setLetterSpacing(.08f); hero.addView(logo);
        TextView sub=tv(tablet?"Redmi Pad 2 • рабочая панель":"Xiaomi 17T • мобильная панель",14,MUTED); hero.addView(sub);
        hero.addView(space(12));
        LinearLayout quick=row();
        String orderCount=String.valueOf(loadList("orders").size()); String clientCount=String.valueOf(loadList("clients").size());
        TextView qs=tv("Заказы: "+orderCount+"    Клиенты: "+clientCount+"    WALLORA Home: активен",14,Color.WHITE); quick.addView(qs); hero.addView(quick);
        addCard(hero);

        String[][] items={
            {"🖨","Print Control","Удалённое управление принтером"}, {"📋","Orders","Заказы и расчёты"}, {"👥","Clients","Клиентская база"}, {"▦","Calculator","м² и стоимость"},
            {"◫","DPI / Size","Размер и DPI"}, {"▣","Portfolio","Галерея работ"}, {"📁","Files","Файлы проекта"}, {"✎","Notes","Рабочие заметки"},
            {"◎","Social","Instagram / TikTok"}, {"✓","Tests","Чек-лист принтера"}, {"⌂","Apps","Быстрый запуск"}, {"⚙","Settings","Система"}
        };
        GridLayout grid=new GridLayout(this); grid.setColumnCount(tablet?4:2); grid.setUseDefaultMargins(false);
        for(int i=0;i<items.length;i++){ final int ix=i; LinearLayout c=card(); c.setGravity(Gravity.CENTER_HORIZONTAL); TextView icon=tv(items[i][0],tablet?32:26,GOLD_LIGHT); icon.setGravity(Gravity.CENTER); c.addView(icon); TextView n=tv(items[i][1],tablet?16:14,Color.WHITE); n.setTypeface(Typeface.DEFAULT_BOLD); n.setGravity(Gravity.CENTER); c.addView(n); TextView d=tv(items[i][2],11,MUTED); d.setGravity(Gravity.CENTER); c.addView(d); c.setOnClickListener(v->dashboardAction(ix)); GridLayout.LayoutParams gp=new GridLayout.LayoutParams(); gp.width=0; gp.height=dp(tablet?142:126); gp.columnSpec=GridLayout.spec(GridLayout.UNDEFINED,1,1f); gp.setMargins(dp(5),dp(5),dp(5),dp(5)); grid.addView(c,gp); }
        page.addView(grid,new LinearLayout.LayoutParams(-1,-2));
    }

    private void dashboardAction(int i){ switch(i){
        case 0: showPrintControl(); break; case 1: showOrders(); break; case 2: showClients(); break; case 3: showCalculator(); break;
        case 4: showDpi(); break; case 5: launchAny("com.google.android.apps.photos","com.miui.gallery"); break; case 6: launchAny("com.google.android.documentsui","com.mi.android.globalFileexplorer"); break; case 7: showNotes(); break;
        case 8: showSocial(); break; case 9: showTests(); break; case 10: showApps(); break; case 11: showSettings(); break;
    }}

    private void showPrintControl(){
        shell("PRINT CONTROL",true);
        LinearLayout s=card(); s.addView(label("Статус")); TextView ready=tv("●  ГОТОВ К РАБОТЕ",20,Color.rgb(106,210,110)); ready.setTypeface(Typeface.DEFAULT_BOLD); s.addView(ready); s.addView(tv("BetterPrint / UltraPrint работают на Windows-ПК. Здесь запускается удалённое управление.",13,MUTED)); addCard(s);
        LinearLayout rem=card(); rem.addView(label("Remote PC")); Button any=button("Открыть AnyDesk"); any.setOnClickListener(v->launchAny("com.anydesk.anydeskandroid")); rem.addView(any); rem.addView(space(8)); Button team=button("Открыть TeamViewer"); team.setOnClickListener(v->launchAny("com.teamviewer.teamviewer.market.mobile")); rem.addView(team); addCard(rem);
        LinearLayout values=card(); values.addView(label("Быстрые параметры WALLORA")); String[] lines={"Рекомендуемый зазор головы: 5 mm","Контроль стены: AUTO, минимальная ошибка датчика","Перед большим принтом: nozzle test + калибровка","Если печать прервана: записать точную высоту/длину напечатанного участка"}; for(String x:lines) values.addView(tv("• "+x,14,Color.WHITE)); addCard(values);
        Button tests=button("Открыть чек-лист перед печатью"); tests.setOnClickListener(v->showTests()); addCard(tests);
    }

    private ArrayList<String> loadList(String key){ String raw=prefs.getString(key,""); ArrayList<String> out=new ArrayList<>(); if(raw.isEmpty()) return out; for(String x:raw.split("\\n---WALLORA---\\n",-1)) if(!x.trim().isEmpty()) out.add(x); return out; }
    private void saveList(String key,ArrayList<String> list){ StringBuilder b=new StringBuilder(); for(int i=0;i<list.size();i++){ if(i>0)b.append("\n---WALLORA---\n"); b.append(list.get(i)); } prefs.edit().putString(key,b.toString()).apply(); }
    private String[] parts(String s,int count){ String[] a=s.split("\\|",-1); String[] o=new String[count]; for(int i=0;i<count;i++) o[i]=i<a.length?a[i]:""; return o; }
    private String clean(String s){ return s.replace("|","/").replace("\n"," ").trim(); }

    private void showOrders(){
        shell("ORDERS",true); Button add=button("＋ Новый заказ"); add.setOnClickListener(v->orderDialog()); addCard(add);
        ArrayList<String> list=loadList("orders"); if(list.isEmpty()){ LinearLayout e=card(); e.addView(tv("Заказов пока нет. Создай первый заказ — площадь и стоимость посчитаются автоматически.",14,MUTED)); addCard(e); return; }
        Collections.reverse(list); for(String rec:list){ String[] p=parts(rec,7); LinearLayout c=card(); TextView n=tv(p[0],18,GOLD_LIGHT); n.setTypeface(Typeface.DEFAULT_BOLD); c.addView(n); c.addView(tv(p[1]+"   •   "+p[2]+" × "+p[3]+" cm   •   "+p[4]+" m²",14,Color.WHITE)); c.addView(tv("Цена: "+p[5]+" Kč   •   "+p[6],13,MUTED)); addCard(c); }
    }
    private void orderDialog(){
        LinearLayout l=new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); l.setPadding(dp(20),dp(8),dp(20),0); EditText name=edit("Проект / клиент"); EditText w=number("Ширина, cm"); EditText h=number("Высота, cm"); EditText rate=number("Цена за m², Kč"); EditText state=edit("Статус (Подготовка / Печать / Готово)"); for(EditText e:new EditText[]{name,w,h,rate,state}){ l.addView(e,new LinearLayout.LayoutParams(-1,dp(56))); l.addView(space(8)); }
        new AlertDialog.Builder(this).setTitle("Новый заказ").setView(l).setNegativeButton("Отмена",null).setPositiveButton("Сохранить",(d,x)->{ try{ double ww=Double.parseDouble(w.getText().toString().replace(',','.')); double hh=Double.parseDouble(h.getText().toString().replace(',','.')); double rr=rate.getText().toString().trim().isEmpty()?0:Double.parseDouble(rate.getText().toString().replace(',','.')); double area=ww*hh/10000d; double total=area*rr; ArrayList<String> a=loadList("orders"); a.add(clean(name.getText().toString())+"|"+new SimpleDateFormat("dd.MM.yyyy",Locale.getDefault()).format(new Date())+"|"+df.format(ww)+"|"+df.format(hh)+"|"+df.format(area)+"|"+df.format(total)+"|"+clean(state.getText().toString())); saveList("orders",a); showOrders(); }catch(Exception e){ toast("Проверь ширину и высоту"); }}).show();
    }

    private void showClients(){
        shell("CLIENTS",true); Button add=button("＋ Добавить клиента"); add.setOnClickListener(v->clientDialog()); addCard(add); ArrayList<String> list=loadList("clients");
        if(list.isEmpty()){ LinearLayout e=card(); e.addView(tv("Клиентская база пустая.",14,MUTED)); addCard(e); return; }
        Collections.reverse(list); for(String rec:list){ String[] p=parts(rec,4); LinearLayout c=card(); TextView n=tv(p[0],18,GOLD_LIGHT); n.setTypeface(Typeface.DEFAULT_BOLD); c.addView(n); c.addView(tv(p[1]+(p[2].isEmpty()?"":"   •   "+p[2]),13,Color.WHITE)); if(!p[3].isEmpty()) c.addView(tv(p[3],12,MUTED)); if(!p[1].isEmpty()) c.setOnClickListener(v->dial(p[1])); addCard(c); }
    }
    private void clientDialog(){
        LinearLayout l=new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); l.setPadding(dp(20),dp(8),dp(20),0); EditText name=edit("Имя / компания"); EditText phone=edit("Телефон"); phone.setInputType(InputType.TYPE_CLASS_PHONE); EditText city=edit("Город / адрес"); EditText note=edit("Комментарий"); for(EditText e:new EditText[]{name,phone,city,note}){ l.addView(e,new LinearLayout.LayoutParams(-1,dp(56))); l.addView(space(8)); }
        new AlertDialog.Builder(this).setTitle("Новый клиент").setView(l).setNegativeButton("Отмена",null).setPositiveButton("Сохранить",(d,x)->{ if(name.getText().toString().trim().isEmpty()){ toast("Укажи имя клиента"); return; } ArrayList<String> a=loadList("clients"); a.add(clean(name.getText().toString())+"|"+clean(phone.getText().toString())+"|"+clean(city.getText().toString())+"|"+clean(note.getText().toString())); saveList("clients",a); showClients(); }).show();
    }

    private void showCalculator(){
        shell("CALCULATOR",true); LinearLayout c=card(); c.addView(label("Стоимость печати")); EditText w=number("Ширина стены, cm"); EditText h=number("Высота стены, cm"); EditText rate=number("Цена за m², Kč"); TextView result=tv("Введите размеры",22,GOLD_LIGHT); result.setTypeface(Typeface.DEFAULT_BOLD); Button go=button("Рассчитать");
        for(EditText e:new EditText[]{w,h,rate}){ c.addView(e,new LinearLayout.LayoutParams(-1,dp(58))); c.addView(space(8)); } c.addView(go); c.addView(space(14)); c.addView(result); addCard(c);
        go.setOnClickListener(v->{ try{ double ww=num(w),hh=num(h),r=rate.getText().toString().trim().isEmpty()?0:num(rate); double area=ww*hh/10000d; String s=df.format(area)+" m²"; if(r>0)s+="   •   "+df.format(area*r)+" Kč"; result.setText(s); prefs.edit().putString("last_rate",rate.getText().toString()).apply(); }catch(Exception e){ toast("Проверь введённые числа"); }}); rate.setText(prefs.getString("last_rate",""));
    }

    private void showDpi(){
        shell("DPI / SIZE",true); LinearLayout c=card(); c.addView(label("Проверка файла перед печатью")); EditText px=number("Ширина изображения, px"); EditText py=number("Высота изображения, px"); EditText cmx=number("Ширина печати, cm"); EditText cmy=number("Высота печати, cm"); TextView r=tv("DPI появится здесь",18,GOLD_LIGHT); Button go=button("Рассчитать DPI"); for(EditText e:new EditText[]{px,py,cmx,cmy}){ c.addView(e,new LinearLayout.LayoutParams(-1,dp(58))); c.addView(space(8)); } c.addView(go); c.addView(space(14)); c.addView(r); addCard(c);
        LinearLayout guide=card(); guide.addView(label("Ориентир")); guide.addView(tv("72 DPI — минимальный рабочий уровень для крупных настенных изображений.\n100–150 DPI — хороший запас качества, если исходник позволяет.\nГлавное — считать DPI в конечном физическом размере печати.",14,Color.WHITE)); addCard(guide);
        go.setOnClickListener(v->{ try{ double x=num(px),y=num(py),cx=num(cmx),cy=num(cmy); double dx=x/(cx/2.54),dy=y/(cy/2.54); r.setText("По ширине: "+df.format(dx)+" DPI\nПо высоте: "+df.format(dy)+" DPI\nМинимум: "+df.format(Math.min(dx,dy))+" DPI"); }catch(Exception e){ toast("Заполни все четыре значения"); }});
    }
    private double num(EditText e){ return Double.parseDouble(e.getText().toString().trim().replace(',','.')); }

    private void showNotes(){
        shell("NOTES",true); LinearLayout c=card(); EditText n=new EditText(this); n.setTextColor(Color.WHITE); n.setHintTextColor(Color.GRAY); n.setHint("Заметки по заказам, размерам, настройкам принтера…"); n.setGravity(Gravity.TOP); n.setMinLines(tablet?16:12); n.setBackground(bg(PANEL_2,12,Color.rgb(70,70,70),1)); n.setPadding(dp(14),dp(14),dp(14),dp(14)); n.setText(prefs.getString("notes","")); c.addView(n,new LinearLayout.LayoutParams(-1,-2)); c.addView(space(10)); Button save=button("Сохранить заметки"); save.setOnClickListener(v->{ prefs.edit().putString("notes",n.getText().toString()).apply(); toast("Сохранено"); }); c.addView(save); addCard(c);
    }

    private void showTests(){
        shell("PRINTER TESTS",true); String[] tests={"1. Проверить уровень чернил и отсутствие воздуха в магистралях","2. Сделать Nozzle Test — все каналы должны печатать стабильно","3. Проверить Vertical / Horizontal Correct без кривых линий","4. Очистить поверхность и проверить ровность стены","5. AUTO distance: убедиться, что датчик держит стабильное расстояние","6. Сделать маленький цветовой тест 10 × 10 cm","7. Проверить ориентацию / Mirror до большого принта","8. Зафиксировать точку старта и реальные отступы","9. Проверить питание, кабели и свободный ход каретки","10. Только после этого запускать основной рисунок"};
        for(String x:tests){ LinearLayout c=card(); CheckBox cb=new CheckBox(this); cb.setText(x); cb.setTextColor(Color.WHITE); cb.setButtonTintList(android.content.res.ColorStateList.valueOf(GOLD)); c.addView(cb); addCard(c); }
    }

    private void showSocial(){
        shell("SOCIAL",true); String[][] apps={{"Instagram","com.instagram.android"},{"TikTok","com.zhiliaoapp.musically"},{"Threads","com.instagram.barcelona"},{"Photos","com.google.android.apps.photos"},{"Canva","com.canva.editor"}}; for(String[] a:apps){ Button b=button(a[0]); b.setOnClickListener(v->launchAny(a[1])); addCard(b); }
    }
    private void showApps(){
        shell("APPS",true); String[][] apps={{"AnyDesk","com.anydesk.anydeskandroid"},{"TeamViewer","com.teamviewer.teamviewer.market.mobile"},{"Gallery","com.google.android.apps.photos","com.miui.gallery"},{"Files","com.google.android.documentsui","com.mi.android.globalFileexplorer"},{"Chrome","com.android.chrome"},{"Calculator","com.google.android.calculator","com.miui.calculator"},{"Keep / Notes","com.google.android.keep","com.miui.notes"},{"Google Drive","com.google.android.apps.docs"}}; for(String[] a:apps){ Button b=button(a[0]); b.setOnClickListener(v->{ String[] pk=new String[a.length-1]; System.arraycopy(a,1,pk,0,pk.length); launchAny(pk); }); addCard(b); }
    }
    private void showSettings(){
        shell("SETTINGS",true); Button sys=button("Открыть настройки Android"); sys.setOnClickListener(v->startActivity(new Intent(Settings.ACTION_SETTINGS))); addCard(sys); Button home=button("Выбор приложения домашнего экрана"); home.setOnClickListener(v->{ try{ startActivity(new Intent(Settings.ACTION_HOME_SETTINGS)); }catch(Exception e){ startActivity(new Intent(Settings.ACTION_SETTINGS)); }}); addCard(home); Button clear=button("Очистить локальные данные WALLORA"); clear.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Очистить данные?").setMessage("Будут удалены клиенты, заказы и заметки внутри WALLORA Control.").setNegativeButton("Отмена",null).setPositiveButton("Удалить",(d,x)->{ prefs.edit().clear().apply(); showDashboard(); }).show()); addCard(clear);
        LinearLayout about=card(); about.addView(label("Версия")); about.addView(tv("WALLORA Control 2.0\nОдин launcher для Redmi Pad 2 и Xiaomi 17T\nДанные хранятся локально на устройстве.",14,Color.WHITE)); addCard(about);
    }

    private void dial(String p){ try{ startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(p)))); }catch(Exception e){ toast("Не удалось открыть телефон"); } }
    private void launchAny(String... packages){ PackageManager pm=getPackageManager(); for(String p:packages){ try{ Intent i=pm.getLaunchIntentForPackage(p); if(i!=null){ startActivity(i); return; }}catch(Exception ignored){} } toast("Приложение не установлено"); }
    private void toast(String s){ Toast.makeText(this,s,Toast.LENGTH_SHORT).show(); }
}
