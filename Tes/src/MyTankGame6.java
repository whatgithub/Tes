/**
 * 功能:坦克游戏的6.0版
 * 1.画出坦克
 * 2.我的坦克可以上下左右移动
 * 3.可以发射子弹，子弹连发(最多五颗)
 * 4.击中敌人坦克时，敌人坦克消失，敌人击中我的坦克，我的坦克消失(加入爆炸效果)
 * 5.敌人坦克不重叠
 * 6.可以分关
 * 7.可以暂停和继续
 * 8.可以记录玩家成绩
 * 9.操作声音文件
 */


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class MyTankGame6 extends JFrame implements ActionListener {
	// 定义组件
	MyPanel mp = null;
	MyStartPanel msp = null;

	// 菜单条
	JMenuBar jmb = null;
	// 菜单
	JMenu jm1 = null;
	JMenu jm2 = null;
	JMenu jm3 = null;
	// 菜单项
	JMenuItem jmi1 = null;
	JMenuItem jmi2 = null;
	JMenuItem jmi3 = null;
	JMenuItem jmi4 = null;

	// 构造方法
	public MyTankGame6() {
		// 创建组件
		// 创建菜单及菜单选项
		jmb = new JMenuBar();
		jm1 = new JMenu("游戏(G)");
		jm2 = new JMenu("暂停(P键)");
		jm3 = new JMenu("继续(O键)");
		jmi1 = new JMenuItem("开始新游戏(N)");
		jmi2 = new JMenuItem("退出游戏(E)");
		jmi3 = new JMenuItem("存盘退出游戏(C)");
		jmi4 = new JMenuItem("继续上局游戏(S)");
		// 设置助记符
		jm1.setMnemonic('G');
		jm2.setMnemonic('O');
		jmi1.setMnemonic('N');
		jmi2.setMnemonic('E');
		jmi3.setMnemonic('C');
		jmi4.setMnemonic('S');

		// 菜单项加入菜单
		jm1.add(jmi1);
		jm1.add(jmi2);
		jm1.addSeparator();
		jm1.add(jmi3);
		jm1.add(jmi4);

		// 菜单加入菜单条
		jmb.add(jm1);
		jmb.add(jm2);
		jmb.add(jm3);

		// 设置菜单条
		setJMenuBar(jmb);

		// 注册监听器
		jmi1.addActionListener(this);
		jmi2.addActionListener(this);
		jmi3.addActionListener(this);
		jmi4.addActionListener(this);

		// 设置动作命令
		jmi1.setActionCommand("newgame");
		jmi2.setActionCommand("exit");
		jmi3.setActionCommand("saveExit");
		jmi4.setActionCommand("continue");

		msp = new MyStartPanel();
		add(msp);

		// 启动线程
		Thread t = new Thread(msp);
		t.start();

		// 设置窗体
		setSize(1100, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public static void main(String[] args) {
		MyTankGame6 mtg = new MyTankGame6();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("newgame")) {
			// 开始游戏
			// 移除旧的开始面板
			remove(msp);
			if (mp != null) {
				remove(mp);
			}
			// 创建战场面板
			mp = new MyPanel("newGame");
			// 创建画板线程
			Thread t = new Thread(mp);
			// 启动画板线程
			t.start();
			// 添加组件
			add(mp);

			// 注册监听器
			addKeyListener(mp);

			// 显示，刷新JFrame
			setVisible(true);
		} else if (e.getActionCommand().equals("exit")) {
			// 退出
			System.exit(0);
		} else if (e.getActionCommand().equals("saveExit")) {
			// 存盘退出
			// 保存击毁敌人的数量和敌人的坐标
			Recorder.setEts(mp.ets);
			Recorder.keepRecordAndEnemyTank();
			System.exit(0);
		} else if (e.getActionCommand().equals("continue")) {
			//
			// 开始游戏
			// 移除旧的开始面板
			remove(msp);
			if (mp != null) {
				remove(mp);
			}
			// 创建战场面板
			mp = new MyPanel("continue");

			// 创建画板线程
			Thread t = new Thread(mp);
			// 启动画板线程
			t.start();
			// 添加组件
			add(mp);

			// 注册监听器
			addKeyListener(mp);

			// 显示，刷新JFrame
			setVisible(true);
		}

	}

}

// 空面板
// 起分关作用
class MyStartPanel extends JPanel implements Runnable {
	int times = 0;

	public void paint(Graphics g) {
		super.paint(g);
		// 画出背景
		g.fillRect(0, 0, 800, 600);
		if (times % 2 == 0) {
			// 设置颜色
			g.setColor(Color.yellow);
			// 字体设置
			Font myFont = new Font("华文新魏", Font.BOLD, 60);
			g.setFont(myFont);
			// 提示信息
			g.drawString("stage: 1", 300, 300);
		}
	}

	@Override
	public void run() {
		while (true) {
			// 休眠1秒
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
			times++;
			// 重绘
			repaint();
		}
	}
}

// 我的面板
class MyPanel extends JPanel implements KeyListener, Runnable {
	// 按键功能是否有效
	boolean flag = true;

	// 定义一个我的坦克
	Hero hero = null;

	// 定义敌人的坦克
	Vector<EnemyTank> ets = new Vector<EnemyTank>();
	Vector<Node> nodes = new Vector<Node>();

	// 敌人坦克数量
	int enSize = 5;

	// 定义炸弹集合
	Vector<Bomb> bombs = new Vector<Bomb>();

	// 定义三张图片,组成一颗炸弹爆炸效果(爆炸效果图)
	Image image1 = null;
	Image image2 = null;
	Image image3 = null;

	// 构造方法
	public MyPanel(String cOrNew) {
		// 恢复记录
		Recorder.getRecording();
		// 初始化我的坦克位置
		hero = new Hero(10, 10);

		if (cOrNew.equals("newGame")) {
			// 初始化敌人的坦克
			for (int i = 0; i < enSize; i++) {
				// 创建一辆敌人的坦克
				EnemyTank et = new EnemyTank((i + 1) * 70, 0);
				// 颜色
				et.setColor(0);
				// 方向
				et.setDirect(2);
				// 将MyPanel的坦克向量交给敌人坦克
				// 用于判断敌人坦克是否重叠
				et.setEts(ets);

				// 启动敌人坦克
				Thread t = new Thread(et);
				t.start();
				// 给敌人坦克添加一颗子弹
				Shot s = new Shot(et.x + 20, et.y + 60, 2);
				// 加入到敌人子弹向量中
				et.shots.add(s);
				Thread t2 = new Thread(s);
				t2.start();

				// 加入敌人坦克向量中
				ets.add(et);

			}
		} else {
			nodes = new Recorder().getNodesAndEnNums();
			// 初始化敌人的坦克
			for (int i = 0; i < nodes.size(); i++) {
				Node node = nodes.get(i);
				// 创建一辆敌人的坦克
				EnemyTank et = new EnemyTank(node.x, node.y);
				// 颜色
				et.setColor(0);
				// 方向
				et.setDirect(node.direct);
				// 将MyPanel的坦克向量交给敌人坦克
				// 用于判断敌人坦克是否重叠
				et.setEts(ets);

				// 启动敌人坦克
				Thread t = new Thread(et);
				t.start();
				// 给敌人坦克添加一颗子弹
				Shot s = new Shot(et.x + 20, et.y + 60, 2);
				// 加入到敌人子弹向量中
				et.shots.add(s);
				Thread t2 = new Thread(s);
				t2.start();

				// 加入敌人坦克向量中
				ets.add(et);
			}
		}

		//播放游戏开始音乐
		AePlayWave apw = new AePlayWave("C:\\start.wav");
		apw.start();
		// 初始化图片
		image1 = Toolkit.getDefaultToolkit().getImage(
				Panel.class.getResource("/images/bomb_1.gif"));
		image2 = Toolkit.getDefaultToolkit().getImage(
				Panel.class.getResource("/images/bomb_2.gif"));
		image3 = Toolkit.getDefaultToolkit().getImage(
				Panel.class.getResource("/images/bomb_3.gif"));

	}

	// 画出提示信息
	public void showInfo(Graphics g) {
		// 画出提示信息坦克(该坦克不参与战斗)
		// 敌人坦克
		Font font = new Font("宋体", Font.PLAIN, 30);
		drawTank(g, 200, 620, 0, 1);
		g.setFont(font);
		g.setColor(Color.black);
		g.drawString(Recorder.getEnNum() + "", 250, 660);
		// 我的坦克
		drawTank(g, 300, 620, 0, 0);
		g.setColor(Color.black);
		g.drawString(Recorder.getMyLife() + "", 350, 660);

		// 画出玩家的总成绩
		g.setColor(Color.black);
		Font f = new Font("宋体", Font.BOLD, 30);
		g.setFont(f);
		g.drawString("您的总成绩", 840, 60);

		drawTank(g, 840, 120, 0, 1);
		g.setColor(Color.black);
		g.drawString(Recorder.getAllEnNum() + "", 900, 160);
	}

	// 重写paint
	public void paint(Graphics g) {
		super.paint(g);
		// 画出背景
		g.fillRect(0, 0, 800, 600);
		// 画出提示信息
		showInfo(g);

		// 画出自己的坦克
		if (hero.isLive) {
			this.drawTank(g, hero.getX(), hero.getY(), hero.direct, 0);
		}
		// 从shots向量中取出每一个子弹
		for (int i = 0; i < hero.shots.size(); i++) {
			Shot myShot = hero.shots.get(i);
			// 画出一颗子弹
			if (myShot != null && myShot.isLive && hero.isLive) {
				g.draw3DRect(myShot.x, myShot.y, 5, 5, false);
			}
			// 如果子弹死亡
			if (myShot.isLive == false) {
				// 从shots向量中删除掉该子弹
				hero.shots.remove(myShot);
			}
		}

		// 画出炸弹
		for (int i = 0; i < bombs.size(); i++) {
			// 取出炸弹
			Bomb b = bombs.get(i);

			if (b.life > 6) {
				g.drawImage(image1, b.x, b.y, 60, 60, this);
			} else if (b.life > 3) {
				g.drawImage(image2, b.x, b.y, 60, 60, this);
			} else {
				g.drawImage(image3, b.x, b.y, 60, 60, this);
			}
			// 减少取出炸弹的生命值
			b.lifeDown();
			// 如果炸弹生命值为≤0，就把该炸弹从向量中删除
			if (b.life <= 0) {
				bombs.remove(b);
			}
		}

		// 画出敌人的坦克
		for (int i = 0; i < ets.size(); i++) {
			EnemyTank et = ets.get(i);
			if (et.isLive) {
				drawTank(g, et.getX(), et.getY(), et.getDirect(), 1);

				// 画出敌人的子弹
				for (int j = 0; j < et.shots.size(); j++) {
					// 取出子弹
					Shot enemyShot = et.shots.get(j);
					if (enemyShot.isLive) {
						g.draw3DRect(enemyShot.x, enemyShot.y, 5, 5, false);
					} else {
						// 如果敌人的子弹死亡就从向量中去掉
						et.shots.remove(enemyShot);
					}
				}
			}
		}
	}

	// 判断我的子弹是否击中敌人的坦克
	public void hitEnemyTank() {
		// 判断是否击中敌人坦克
		for (int i = 0; i < hero.shots.size(); i++) {
			// 取出子弹
			Shot myShot = hero.shots.get(i);
			// 判断子弹是否有效
			if (myShot.isLive) {
				// 取出每个坦克，与它判断
				for (int j = 0; j < ets.size(); j++) {
					// 取出坦克
					EnemyTank et = ets.get(j);
					// 敌人的坦克是否存活
					if (et.isLive) {
						hitTank(myShot, et);
					}
				}
			}
		}
	}

	// 敌人的子弹是否击中我
	public void hitMe() {
		// 取出每一个敌人的坦克的每一颗子弹进行判断
		for (int i = 0; i < ets.size(); i++) {
			// 取出敌人的坦克
			EnemyTank et = ets.get(i);
			// 我的坦克和敌人的坦克是否存活
			if (hero.isLive && et.isLive) {
				for (int j = 0; j < et.shots.size(); j++) {
					// 取出子弹
					Shot enemyShot = et.shots.get(j);
					// 判断子弹是否有效
					if (enemyShot.isLive) {
						hitTank(enemyShot, hero);
					}
				}
			}
		}
	}

	// 判断子弹是否击中坦克
	public void hitTank(Shot s, Tank et) {
		// 判断该坦克的方向
		switch (et.direct) {
		// 如果敌人当前方向是向上或者向下
		case 0:
		case 2: {
			if (s.x >= et.x && s.x <= et.x + 40 && s.y >= et.y
					&& s.y <= et.y + 60) {
				// 击中
				if (et.equals(hero)) {
					// 减少自己坦克生命数
					Recorder.reduceMyLife();
				} else {
					if (et.isLive) {
						// 减少敌人坦克数量
						Recorder.reduceEnNum();
						// 增加我的战绩
						Recorder.addEnNum();
					}
				}
				// 子弹死亡
				s.isLive = false;
				// 敌人坦克死亡
				et.isLive = false;
				// 创建一颗炸弹
				Bomb b = new Bomb(et.x, et.y);
				// 放入向量中
				bombs.add(b);
			}
			break;
		}
		case 1:
		case 3: {
			if (s.x >= et.x && s.x <= et.x + 60 && s.y >= et.y
					&& s.y <= et.y + 40) {
				// 击中
				if (et.equals(hero)) {
					// 减少自己坦克生命数
					Recorder.reduceMyLife();
				} else {
					if (et.isLive) {
						// 减少敌人坦克数量
						Recorder.reduceEnNum();
						// 增加我的战绩
						Recorder.addEnNum();
					}
				}
				// 子弹死亡
				s.isLive = false;
				// 敌人坦克死亡
				et.isLive = false;
				// 创建一颗炸弹
				Bomb b = new Bomb(et.x, et.y);
				// 放入向量中
				bombs.add(b);
			}
			break;
		}
		}
	}

	// 画出坦克的方法(扩展)
	public void drawTank(Graphics g, int x, int y, int direct, int type) {
		// 判断是什么颜色的坦克
		switch (type) {
		case 0:
			g.setColor(Color.red);
			break;
		case 1:
			g.setColor(Color.yellow);
			break;
		}
		// 判断方向
		switch (direct) {
		// 向上
		case 0:
			// 根据坦克位置
			// 画出我的坦克
			// 1.画出左边的矩形
			g.fill3DRect(x, y, 10, 60, false);
			// 2.画出右边的矩形
			g.fill3DRect(x + 30, y, 10, 60, false);
			// 3.画出中间矩形
			g.fill3DRect(x + 10, y + 10, 20, 40, false);
			// 4.画出圆形
			g.fillOval(x + 10, y + 20, 20, 20);
			// 5.画出线
			g.drawLine(x + 20, y, x + 20, y + 30);
			break;
		// 向右
		case 1:
			// 根据坦克位置
			// 画出我的坦克
			// 画出上边的矩形
			g.fill3DRect(x, y, 60, 10, false);
			// 画出下边的矩形
			g.fill3DRect(x, y + 30, 60, 10, false);
			// 画出中间矩形
			g.fill3DRect(x + 10, y + 10, 40, 20, false);
			// 画出圆形
			g.fillOval(x + 20, y + 10, 20, 20);
			// 画出线
			g.drawLine(x + 60, y + 20, x + 30, y + 20);
			break;
		// 向下
		case 2:
			// 根据坦克位置
			// 画出我的坦克
			// 1.画出左边的矩形
			g.fill3DRect(x, y, 10, 60, false);
			// 2.画出右边的矩形
			g.fill3DRect(x + 30, y, 10, 60, false);
			// 3.画出中间矩形
			g.fill3DRect(x + 10, y + 10, 20, 40, false);
			// 4.画出圆形
			g.fillOval(x + 10, y + 20, 20, 20);
			// 5.画出线
			g.drawLine(x + 20, y + 60, x + 20, y + 30);
			break;
		// 向左
		case 3:
			// 根据坦克位置
			// 画出我的坦克
			// 画出上边的矩形
			g.fill3DRect(x, y, 60, 10, false);
			// 画出下边的矩形
			g.fill3DRect(x, y + 30, 60, 10, false);
			// 画出中间矩形
			g.fill3DRect(x + 10, y + 10, 40, 20, false);
			// 画出圆形
			g.fillOval(x + 20, y + 10, 20, 20);
			// 画出线
			g.drawLine(x, y + 20, x + 30, y + 20);
		}
	}

	// 按键监听接口实现
	// 输入某一信息
	@Override
	public void keyTyped(KeyEvent e) {
	}

	// 按键处理，a表示向左、 s表示向下、d表示向右、w表示向上
	// 按下
	@Override
	public void keyPressed(KeyEvent e) {
		if (flag) {
			// 设置我的坦克的方向
			// 上 下 左 右
			// 设置方向
			// 向那一方向移动
			if (e.getKeyCode() == KeyEvent.VK_W) {
				hero.setDirect(0);
				hero.moveUp();
			} else if (e.getKeyCode() == KeyEvent.VK_S) {
				hero.setDirect(2);
				hero.moveDown();
			} else if (e.getKeyCode() == KeyEvent.VK_A) {
				hero.setDirect(3);
				hero.moveLeft();
			} else if (e.getKeyCode() == KeyEvent.VK_D) {
				hero.setDirect(1);
				hero.moveRight();
			}

			// 发射子弹
			if (e.getKeyCode() == KeyEvent.VK_J && hero.isLive) {
				// 测试自己坦克子弹当前数量
				// System.out.println("size=" + hero.shots.size());
				if (hero.shots.size() <= 4) {
					hero.shotEnemy();
				}
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_P) {
			// 暂停
			// 使按键失效
			// 则我的坦克不能移动和发射子弹
			flag = false;
			// 发射出的子弹停止
			for (Shot s : hero.shots) {
				s.speed = 0;
			}
			// 使敌人坦克不能移动和发射子弹
			for (EnemyTank et : ets) {
				et.isMove = false;
				et.isShot = false;
				et.setSpeed(0);
				for (Shot s : et.shots) {
					s.speed = 0;
				}
			}

		}
		if (e.getKeyCode() == KeyEvent.VK_C) {
			// 继续
			// 使按键生效
			// 则我的坦克可以移动和发射子弹
			flag = true;
			// 发射出的子弹继续移动
			for (Shot s : hero.shots) {
				s.speed = 10;
			}
			// 敌人坦克继续移动和发射子弹
			for (EnemyTank et : ets) {
				et.isMove = true;
				et.isShot = true;
				et.setSpeed(5);
				for (Shot s : et.shots) {
					s.speed = 10;
				}
			}
		}
		// 重绘Panel
		repaint();

	}

	// 松开
	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void run() {
		while (true) {
			try {
				// 100毫秒重绘一次
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// 击中敌人坦克
			hitEnemyTank();
			// 敌人的坦克击中我的坦克
			hitMe();
			// 重绘
			repaint();
		}
	}
}