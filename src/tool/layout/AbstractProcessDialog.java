package tool.layout;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public abstract class AbstractProcessDialog extends AbstractDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2563784175151749195L;

	public AbstractProcessDialog(String title, int posX, int posY, int width) {
		super(title, posX, posY, width);
		// TODO Auto-generated constructor stub
	}

	public AbstractProcessDialog(String title, JPanel parentComponent) {
		super(title, parentComponent);
	}

	@Override
	protected void returnValue() {
		// TODO Auto-generated method stub

	}

	public abstract void setText(String str);

	public abstract void setHint(String str);

	public abstract String getText();

	public abstract String getHint();

	/**
	 * 返回一个带有不确定进度条的提示窗
	 * 
	 * @param title
	 * @param Message
	 * @param parentComponent
	 * @return
	 */
	public static AbstractProcessDialog showProgress(String title, String Message, String hint,
			JPanel parentComponent) {
		JProgressBar progress = new JProgressBar(0, 100);
		final JLabel text_msg = new JLabel(Message);
		final JLabel text_hint = new JLabel(hint);
		progress.setIndeterminate(true);
		text_msg.setHorizontalAlignment(SwingConstants.CENTER);
		text_hint.setHorizontalAlignment(SwingConstants.CENTER);
		AbstractProcessDialog dialog_progress = new AbstractProcessDialog(title, parentComponent) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1781796791056677935L;

			@Override
			public void setText(final String str) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						text_msg.setText(str);
					}
				});

			}

			public void setHint(final String str) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						text_hint.setText(str);
					}
				});
			}

			@Override
			public String getText() {
				return text_msg.getText();
			}

			@Override
			protected void returnValue() {
				// TODO Auto-generated method stub

			}

			@Override
			public String getHint() {
				// TODO Auto-generated method stub
				return text_hint.getText();
			}
		};
		dialog_progress.setLayout(new GridLayout(3, 1, 3, 5));
		dialog_progress.add(text_msg);
		dialog_progress.add(progress);
		dialog_progress.add(text_hint);
		return dialog_progress;
	}

}
