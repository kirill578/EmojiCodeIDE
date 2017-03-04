package kkulakov.emojicode.shell;

/**
 * Author: jinghao
 * Email: jinghao@meizu.com
 * Date: 2016-07-05
 */
public interface OnExecuteCallback {

    void onSuccess(String buffer);

    void onFail();
}
