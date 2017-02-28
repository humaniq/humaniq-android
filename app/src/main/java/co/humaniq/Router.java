package co.humaniq;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import co.humaniq.views.*;

import java.util.HashMap;


public class Router {
    private final static String TAG = "Router";

    // В качестве нумерации использовуется следующий формат:
    // первые две цифры - номер страници/экрана и все что к нему относится, к пример
    // к главной странице относятся - сам экран главная, тур, новости и полезные советы
    // первые две цифры = 20, Когда заканчиваются цифры, к примеру 90 уже есть, заполняется
    // вторая цифра, к примеру 11.
    //
    // Вторе две цифры - номер секции, к примеру тур - 01, новости - 02 итд.
    //
    // Это необходимо чтобы было меньше конфликтов при слиянии веток.

    public static Integer LOADING = 1000;
    public static Integer LOGIN = 2000;
    public static Integer REGISTER = 2001;

    public static Integer DASHBOARD = 3000;

    private static Integer lastScreen = null;

    private static class Route {
        String title = "";
        Class klass;

        Route(final Class klass, final String title) {
            this.title = title;
            this.klass = klass;
        }

        Route(final Class klass) {
            this.klass = klass;
        }
    }

    private static HashMap<Integer, Route> routes = new HashMap<>();
    static {
        routes.put(LOADING, new Route(LoadingActivity.class));
        routes.put(LOGIN, new Route(LoginActivity.class));
        routes.put(REGISTER, new Route(RegisterActivity.class));
        routes.put(DASHBOARD, new Route(DashboardActivity.class));
    }

    private Router() {}

    private static Bundle bundle = null;

    public static Integer getLastScreen() {
        return lastScreen;
    }

    public static void setBundle(Bundle bundle) {
        Router.bundle = bundle;
    }

    public static void goActivity(final ViewContext context, final Integer screenCode) {
        goActivity(context, screenCode, null);
    }

    public static void goActivity(final ViewContext context, final Integer screenCode,
                                  final Integer requestCode)
    {
        Bundle bundle = Router.bundle;
        Router.bundle = null;

        Class activityClass = routes.get(screenCode).klass;
        String title = routes.get(screenCode).title;

        if (activityClass == null)
            return;

        Object activityInstance ;
        BaseActivity activity = context.getActivityInstance();

        if (!title.equals(""))
            activity.setTitle(title);

        try {
            activityInstance = activityClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        assert activityInstance != null;

        lastScreen = screenCode;
        Intent intent = new Intent(activity, activityClass);

        if (bundle != null)
            intent.putExtras(bundle);

        if (requestCode == null)
            activity.startActivity(intent);
        else
            activity.startActivityForResult(intent, requestCode);
    }

    public static void goFragment(final ViewContext context, final Integer screenCode) {
        Bundle bundle = Router.bundle;
        Router.bundle = null;

        Class fragmentClass = routes.get(screenCode).klass;
        String title = routes.get(screenCode).title;

        if (fragmentClass == null)
            return;

        Object fragmentInstance = null;
        BaseActivity activity = context.getActivityInstance();

        try {
            fragmentInstance = fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        assert fragmentInstance != null;

        if (!title.equals(""))
            activity.setTitle(title);

        lastScreen = screenCode;

        if (bundle != null)
            ((BaseFragment) fragmentInstance).setArguments(bundle);

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.contentLayout, (BaseFragment) fragmentInstance)
                .commit();
    }
}
