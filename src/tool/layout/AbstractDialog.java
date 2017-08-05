package tool.layout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public abstract class AbstractDialog extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7058295238323126351L;

	public AbstractDialog(String title, int posX, int posY, int width) {
		setTitle(title);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocation(posX, posY);
		setSize(width, width / 3);
	}

	public AbstractDialog(String title, JPanel parentComponent) {
		setTitle(title);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocation(parentComponent.getLocationOnScreen());
		setSize(parentComponent.getWidth() / 2, parentComponent.getWidth() / 5);
	}

	/**
	 * 用于与主窗口交互数据
	 * 
	 * @author Mr_Li
	 */
	protected abstract void returnValue();

}
