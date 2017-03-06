package co.humaniq.services;

import co.humaniq.views.ViewContext;


abstract public class DataService<T> {
    private ViewContext context;

    public ViewContext getContext() {
        return context;
    }

    DataService(ViewContext context) {
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
