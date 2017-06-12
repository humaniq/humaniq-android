package co.humaniq;

import android.content.Intent;
import android.os.Bundle;
import co.humaniq.views.*;
import co.humaniq.views.take_photo.TakePhotoActivity;

import java.util.HashMap;


public class Router {
    private final static String TAG = "Router";

    public static Integer GREETER = 1000;
    public static Integer REGISTER = 2000;
    public static Integer TAKE_PHOTO = 2001;

    public static Integer DASHBOARD = 3000;
    public static Integer VIDEO = 5000;
    public static Integer GRAPHIC_KEY = 6000;

    private static Integer lastScreen = null;

    private static class Route {
        String title = "";
        Class klass;

        Route(final Class klass, final String title) {
            this.klass = klass;
        }

        Route(final Class klass) {
            this.klass = klass;
        }
    }

    private static HashMap<Integer, Route> routes = new HashMap<>();
    static {
        routes.put(GREETER, new Route(GreeterActivity.class));
        routes.put(DASHBOARD, new Route(DashboardActivity.class));
        routes.put(TAKE_PHOTO, new Route(TakePhotoActivity.class));
        routes.put(VIDEO, new Route(HelpVideoActivity.class));
        routes.put(GRAPHIC_KEY, new Route(GraphicKeyActivity.class));
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
}
