package kkulakov.emojicode.shell;

/**
 * Author: jinghao
 * Email: jinghao@meizu.com
 * Date: 2016-07-04
 */
public interface Shell {

    int exec(String... args);

    void close();
}
