package ir.iribcs.sso;

public interface OnAsyncTaskListener<T> {
    public void onSuccess(String result);
    public void onFailure(String error);
}