package jp.massango.winfetch;

import javax.swing.*;

//TIP コードを<b>実行</b>するには、<shortcut actionId="Run"/> を押すか
// ガターの <icon src="AllIcons.Actions.Execute"/> アイコンをクリックします。
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Yeah::new);
    }
}
