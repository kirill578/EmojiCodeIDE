package kkulakov.emojicode.shell;

/**
 * Author: jinghao
 * Email: jinghao@meizu.com
 * Date: 2016-07-05
 */
public class FakeShellKeeper {

    private static FakeShellKeeper ourInstance;

    private ShellImpl mShell;

    public static FakeShellKeeper getInstance() {
        if (ourInstance == null) {
            ourInstance = new FakeShellKeeper();
        }
        return ourInstance;
    }

    private FakeShellKeeper() {
        this.mShell = new ShellImpl();
    }

    public void exec(String... args){
        mShell.exec(args);
    }

    public void restart(){
        mShell.close();
        mShell = new ShellImpl();
    }

    public void close(){
        mShell.close();
        ourInstance = null;
    }

    public void setCallBack(OnExecuteCallback callBack){
        mShell.setOnExecuteCallback(callBack);
    }

}
