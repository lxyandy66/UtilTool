package tool.graph.plot;

//filename: DrawPanel.java
//RobinTang
//2012-08-23

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

/**@deprecated
 * 这是原始版本的绘图Panel，构建简单没有监听器，效率更高，可以满足基本的绘制波形，有所修改
 * 
 * @author RobinTang
 * @author Mr_Li
 *
 */
public class DrawPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5756806107516049056L;
	
	public DrawPanel() {
		super();
		setBackground(Color.WHITE);
	}

	/**
	 * 导入音频数据，通过WaveFileReader所得的音频
	 * 
	 * @param data
	 * @author Mr_Li
	 */
	public void importData(int[] data) {
	}

	// @Override
	// protected void paintComponent(Graphics g)
	// {//这个逻辑不对，这个函数直接在构造的时候就会调用，思路应该是生成一个空面板，之后再调用方法
	public void paintWave(int[] data) {
		if (data == null)
			throw new NullPointerException("数据没有初始化");
		Graphics g = this.getGraphics();
		int ww = getWidth();
		int hh = getHeight();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, ww, hh);

		int len = data.length;
		int step = len / ww;
		if (step == 0)
			step = 1;

		int prex = 0, prey = 0; // 上一个坐标
		int x = 0, y = 0;

		g.setColor(Color.BLUE);
		double k = hh / 2.0 / 32768.0;
		for (int i = 0; i < ww; ++i) {
			x = i;

			// 下面是个三点取出并绘制
			// 实际中应该按照采样率来设置间隔
			y = hh - (int) (data[i * 3] * k + hh / 2);

			System.out.print(y);
			System.out.print(" ");

			if (i != 0) {
				g.drawLine(x, y, prex, prey);
			}
			prex = x;
			prey = y;
		}
	}

}
