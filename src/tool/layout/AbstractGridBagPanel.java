package tool.layout;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public abstract class AbstractGridBagPanel extends JPanel implements ActionListener {

	// 项目中所有布局以此为基础
	/**
	 * 
	 */
	private static final long serialVersionUID = 317499279116558548L;

	protected GridBagLayout layout_gridbag = new GridBagLayout();
	protected GridBagConstraints constraints = new GridBagConstraints();// 这个类是用来控制GridBag的

	public AbstractGridBagPanel() {
		if (System.getProperty("os.name").toLowerCase().indexOf("mac") == -1)
			try {//如果系统不是mac就换个风格
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			} catch (Exception e) {

			}
		constraints.weightx = 1;
		constraints.weighty = 1;
		setLayout(layout_gridbag);
	}

	/**
	 * 用于反应拖拽事件的接口
	 * 
	 * @author Mr_Li
	 *
	 */
	protected interface DropReactor {
		/**
		 * 对得到拖拽文件做出的响应
		 * 
		 * @param list
		 *            拖拽得到的文件
		 */
		public void onFileDrop(List<File> list);// 这个能不能直接作为一个接口传进去/*Java 8可以*/
	};

	/**
	 * @author Mr_Li
	 * @param component
	 *            接收拖拽文件的控件
	 * @return 返回一个DropTarget对象
	 * 
	 *         对于拖拽文件的操作，通过类中抽象的onFileDrop()方法实现
	 */
	public DropTarget initFileDropTarget(Component component, final DropReactor action) {
		return new DropTarget(component, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {

			@Override
			public void drop(DropTargetDropEvent dtde) {
				// TODO Auto-generated method stub
				try {
					if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						// 接收拖拽来的数据
						dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
						@SuppressWarnings("unchecked")
						List<File> list = (List<File>) (dtde.getTransferable()
								.getTransferData(DataFlavor.javaFileListFlavor));
						action.onFileDrop(list);// 对应的操作
						// 指示拖拽操作已完成
						dtde.dropComplete(true);
					} else {
						// 拒绝拖拽来的数据
						dtde.rejectDrop();
					}
				} catch (UnsupportedFlavorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	protected void addComponent(Component component, int row, int column, int wid, int high) {
		// 用来添加控件，原来这个函数要自己写？？？******还有这个能不能变成个工具函数
		if (row < 0 || column < 0 || wid < 0 || high < 0)
			// throw new Exception("Arg is Wrong when add " +
			// component.toString() + " : must be non-negative number");
			return;// 懒得丢异常了，反正是private
		constraints.gridx = column;
		constraints.gridy = row;
		constraints.gridwidth = wid;
		constraints.gridheight = high;
		layout_gridbag.setConstraints(component, constraints);
		add(component);
	}
}
