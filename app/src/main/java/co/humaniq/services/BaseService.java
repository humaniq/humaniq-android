package co.humaniq.services;

import co.humaniq.views.ViewContext;


public class BaseService<T> {
    private ViewContext context;

    public ViewContext getContext() {
        return context;
    }

    BaseService(ViewContext context) {
        this.context = context;
    }

    /**
     * Получить все записи
     * Возвращает в context.success список ArrayList<ModelT>
     */
    public void all() {
        throw new UnsupportedOperationException();
    }

    /**
     * Получить запись по id
     * Возвращает в context.success запись ModelT
     */
    public void get(final int id) {
        throw new UnsupportedOperationException();
    }
}
