

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

//播放声音的类
class AePlayWave extends Thread {

	//文件名
	private String filename;
	
	public AePlayWave(String wavfile) {
		filename = wavfile;

	}
	
	@Override
	public void run() {

		File soundFile = new File(filename);
		//音频输入流
		AudioInputStream audioInputStream = null;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}

		AudioFormat format = audioInputStream.getFormat();
		SourceDataLine auline = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

		try {
			auline = (SourceDataLine) AudioSystem.getLine(info);
			auline.open(format);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		auline.start();
		int nBytesRead = 0;
		byte[] abData = new byte[512];

		try {
			while (nBytesRead != -1) {
				nBytesRead = audioInputStream.read(abData, 0, abData.length);
				if (nBytesRead >= 0)
					auline.write(abData, 0, nBytesRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} finally {
			auline.drain();
			auline.close();
		}
	}
}

//恢复点
class Node {
	int x;
	int y;
	int direct;

	public Node(int x, int y, int direct) {
		this.x = x;
		this.y = y;
		this.direct = direct;
	}
}

// 记录类,保存玩家的设置
class Recorder {
	// 记录每关有多少敌人
	private static int enNum = 20;
	// 设置我的生命数
	private static int myLife = 3;
	// 记录一共消灭了多少敌人
	private static int allEnNum = 0;
	// 从文件中恢复记录点
	static Vector<Node> nodes = new Vector<Node>();
	// 敌人坦克向量
	private static Vector<EnemyTank> ets = new Vector<EnemyTank>();

	// 定义文件流
	private static FileWriter fw = null;
	private static BufferedWriter bw = null;
	private static FileReader fr = null;
	private static BufferedReader br = null;

	// 从文件中读取记录
	public Vector<Node> getNodesAndEnNums() {
		// 如果nodes里有向量
		if (nodes != null) {
			nodes.removeAllElements();
		}
		try {
			// 创建文件流
			fr = new FileReader("C:\\myRecording.txt");
			br = new BufferedReader(fr);
			String str = "";
			// 先读取第一行
			str = br.readLine();
			allEnNum = Integer.parseInt(str);
			while ((str = br.readLine()) != null) {
				String[] strs = str.split(" ");
				Node node = new Node(Integer.parseInt(strs[0]),
						Integer.parseInt(strs[1]), Integer.parseInt(strs[2]));
				nodes.add(node);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 后打开先关闭
				br.close();
				fr.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
		return nodes;
	}

	// 保存击毁敌人的数量和敌人的坦克坐标和方向
	public static void keepRecordAndEnemyTank() {
		try {
			// 创建文件流
			fw = new FileWriter("C:\\myRecording.txt");
			bw = new BufferedWriter(fw);

			bw.write(allEnNum + "\r\n");

			// 保存当前活的敌人坦克和坐标和方向
			for (EnemyTank et : ets) {
				// 取出坦克
				if (et.isLive) {
					// 活着则保存
					String x = et.getX() + " ";
					String y = et.getY() + " ";
					String direct = et.getDirect() + "";

					// 写入
					bw.write(x);
					bw.write(y);
					bw.write(direct + "\r\n");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭流
			try {
				// 后开先关
				bw.close();
				fw.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	// 从文件中读取记录
	public static void getRecording() {
		try {
			// 创建文件流
			fr = new FileReader("C:\\myRecording.txt");
			br = new BufferedReader(fr);
			String str = br.readLine();
			allEnNum = Integer.parseInt(str);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 后打开先关闭
				br.close();
				fr.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	// 把玩家击毁敌人坦克数量保存到文件中
	public static void keepRecording() {
		try {
			// 创建文件流
			fw = new FileWriter("C:\\myRecording.txt");
			bw = new BufferedWriter(fw);

			bw.write(allEnNum + "\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭流
			try {
				// 后开先关
				bw.close();
				fw.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	// 消灭敌人
	public static void addEnNum() {
		Recorder.allEnNum++;
	}

	// 减少敌人数
	public static void reduceEnNum() {
		Recorder.enNum--;
		// 如果小于等于0，则恒为0
		if (enNum <= 0) {
			Recorder.enNum = 0;
		}
	}

	// 减少自己生命数
	public static void reduceMyLife() {
		Recorder.myLife--;
	}

	public static int getEnNum() {
		return enNum;
	}

	public static void setEnNum(int enNum) {
		Recorder.enNum = enNum;
	}

	public static int getMyLife() {
		return myLife;
	}

	public static void setMyLife(int myLife) {
		Recorder.myLife = myLife;
	}

	public static int getAllEnNum() {
		return allEnNum;
	}

	public static void setAllEnNum(int allEnNum) {
		Recorder.allEnNum = allEnNum;
	}

	public static Vector<EnemyTank> getEts() {
		return ets;
	}

	public static void setEts(Vector<EnemyTank> ets) {
		Recorder.ets = ets;
	}
}

// 炸弹类
class Bomb {
	int x; // 横坐标
	int y; // 纵坐标
	int life = 9; // 炸弹的生命值
	boolean isLive = true; // 是否存活，炸弹生命值≤0，死亡

	public Bomb(int x, int y) {
		this.x = x;
		this.y = y;
	}

	// 减少生命值
	public void lifeDown() {
		if (life > 0) {
			life--;
		} else {
			isLive = false;
		}
	}

}

// 子弹类
class Shot implements Runnable {
	int x; // 横坐标
	int y; // 纵坐标
	int direct; // 方向
	int speed = 10; // 子弹速度
	boolean isLive = true;// 是否存活

	public Shot(int x, int y, int direct) {
		this.x = x;
		this.y = y;
		this.direct = direct;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				e.printStackTrace();
			}
			switch (direct) {
			case 0: {
				// 向上
				y -= speed;
				break;
			}
			case 1: {
				// 向右
				x += speed;
				break;
			}
			case 2: {
				// 向下
				y += speed;
				break;
			}
			case 3: {
				// 向左
				x -= speed;
				break;
			}
			}
			// 测试子弹坐标
			// System.out.println(x + " " + y);
			// 子弹消失情况
			// 1.子弹是否碰到边缘
			if (x < 0 || x > 800 || y < 0 || y > 600) {
				isLive = false;
				break;
			}
		}

	}
}

// 坦克类
class Tank {
	int x = 0; // 坦克横坐标
	int y = 0; // 坦克纵坐标
	// 0 向上、1 向右、 2 向下 、3 向左
	int direct = 0; // 坦克方向
	int speed = 5; // 坦克的速度
	int color; // 颜色 0红色，1黄色
	boolean isLive = true; // 是否存活

	// 获取坦克颜色
	public int getColor() {
		return color;
	}

	// 设置坦克颜色
	public void setColor(int color) {
		this.color = color;
	}

	// 获取坦克速度
	public int getSpeed() {
		return speed;
	}

	// 设置坦克速度
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	// 获取坦克方向
	public int getDirect() {
		return direct;
	}

	// 设置坦克方向
	public void setDirect(int direct) {
		this.direct = direct;
	}

	// 获取横坐标
	public int getX() {
		return x;
	}

	// 设置横坐标
	public void setX(int x) {
		this.x = x;
	}

	// 获取纵坐标
	public int getY() {
		return y;
	}

	// 设置纵坐标
	public void setY(int y) {
		this.y = y;
	}

	// 构造方法
	public Tank(int x, int y) {
		this.x = x;
		this.y = y;
	}
}

// 我的坦克
class Hero extends Tank {

	// 定义一个向量，可以访问到MyPanel上所有敌人的坦克
	Vector<EnemyTank> ets = new Vector<EnemyTank>();

	// 子弹
	// 临时子弹
	Shot s = null;
	// 将临时子弹添加到向量中
	Vector<Shot> shots = new Vector<Shot>();

	// 得到MyPanel的敌人坦克向量
	public void setEts(Vector<EnemyTank> ets) {
		this.ets = ets;
	}

	// 构造方法
	public Hero(int x, int y) {
		super(x, y);
	}

	// 坦克向上移动
	public void moveUp() {
		if (y > 0) {
			y -= speed;
		}
	}

	// 坦克向右移动
	public void moveRight() {
		if (x < 730) {
			x += speed;
		}
	}

	// 坦克向下移动
	public void moveDown() {
		if (y < 500) {
			y += speed;
		}
	}

	// 坦克向左移动
	public void moveLeft() {
		if (x > 0) {
			x -= speed;
		}
	}

	// 开火
	public void shotEnemy() {

		// 创建一颗子弹
		// 把子弹加入到向量中
		// 上 右 下 左
		switch (direct) {
		case 0: {
			s = new Shot(x + 20, y, 0);
			shots.add(s);
			break;
		}
		case 1: {
			s = new Shot(x + 60, y + 20, 1);
			shots.add(s);
			break;
		}
		case 2: {
			s = new Shot(x + 20, y + 60, 2);
			shots.add(s);
			break;
		}
		case 3: {
			s = new Shot(x, y + 20, 3);
			shots.add(s);
			break;
		}
		}
		// 创建子弹线程
		Thread t = new Thread(s);
		// 启动子弹线程
		t.start();
	}
}

// 敌人的坦克,实现线程
class EnemyTank extends Tank implements Runnable {
	// 定义一个向量，可以访问到MyPanel上所有敌人的坦克
	Vector<EnemyTank> ets = new Vector<EnemyTank>();
	// 定义一个向量，可以存放敌人的子弹
	Vector<Shot> shots = new Vector<Shot>();
	// 敌人是否发出子弹
	boolean isShot = true;
	// 敌人是否移动
	boolean isMove = true;

	// 构造方法
	public EnemyTank(int x, int y) {
		super(x, y);
	}

	// 得到MyPanel的敌人坦克向量
	public void setEts(Vector<EnemyTank> ets) {
		this.ets = ets;
	}

	// 判断是否碰到了别的敌人坦克
	public boolean isTouchOtherEnemy() {
		boolean b = false;

		switch (direct) {
		case 0: {
			// 当前敌人坦克向上
			// 取出所有的敌人坦克
			for (int i = 0; i < ets.size(); i++) {
				// 取出第一个坦克
				EnemyTank et = ets.get(i);
				// 如果不是自己
				if (et != this) {
					// 如果敌人的方向是向下或者向上
					if (et.direct == 2 || et.direct == 0) {
						// 如果重叠
						// 返回true
						if (this.x >= et.x && this.x <= et.x + 40
								&& this.y >= et.y && this.y <= et.y + 60) {
							return true;
						}
						if (this.x + 40 >= et.x && this.x + 40 <= et.x + 40
								&& this.y >= et.y && this.y <= et.y + 60) {
							return true;
						}
					}
					// 如果敌人的方向是向左或者向右
					if (et.direct == 3 || et.direct == 1) {
						// 如果重叠
						// 返回true
						if (this.x >= et.x && this.x <= et.x + 60
								&& this.y >= et.y && this.y <= et.y + 40) {
							return true;
						}
						if (this.x + 40 >= et.x && this.x + 40 <= et.x + 60
								&& this.y >= et.y && this.y <= et.y + 40) {
							return true;
						}
					}
				}
			}
			break;
		}
		case 1: {
			// 当前敌人坦克向右
			// 取出所有的敌人坦克
			for (int i = 0; i < ets.size(); i++) {
				// 取出第一个坦克
				EnemyTank et = ets.get(i);
				// 如果不是自己
				if (et != this) {
					// 如果敌人的方向是向下或者向上
					if (et.direct == 2 || et.direct == 0) {
						// 如果重叠
						// 返回true
						if (this.x + 60 >= et.x && this.x + 60 <= et.x + 40
								&& this.y >= et.y && this.y <= et.y + 60) {
							return true;
						}
						if (this.x + 60 >= et.x && this.x + 60 <= et.x + 40
								&& this.y + 40 >= et.y
								&& this.y + 40 <= et.y + 60) {
							return true;
						}
					}
					// 如果敌人的方向是向左或者向右
					if (et.direct == 3 || et.direct == 1) {
						// 如果重叠
						// 返回true
						if (this.x + 60 >= et.x && this.x + 60 <= et.x + 60
								&& this.y >= et.y && this.y <= et.y + 40) {
							return true;
						}
						if (this.x + 60 >= et.x && this.x + 60 <= et.x + 60
								&& this.y + 40 >= et.y
								&& this.y + 40 <= et.y + 40) {
							return true;
						}
					}
				}
			}
			break;
		}
		case 2: {
			// 当前敌人坦克向下
			// 取出所有的敌人坦克
			for (int i = 0; i < ets.size(); i++) {
				// 取出第一个坦克
				EnemyTank et = ets.get(i);
				// 如果不是自己
				if (et != this) {
					// 如果敌人的方向是向下或者向上
					if (et.direct == 2 || et.direct == 0) {
						// 如果重叠
						// 返回true
						if (this.x >= et.x && this.x <= et.x + 40
								&& this.y + 60 >= et.y
								&& this.y + 60 <= et.y + 60) {
							return true;
						}
						if (this.x + 40 >= et.x && this.x + 40 <= et.x + 40
								&& this.y + 60 >= et.y
								&& this.y + 60 <= et.y + 60) {
							return true;
						}
					}
					// 如果敌人的方向是向左或者向右
					if (et.direct == 3 || et.direct == 1) {
						// 如果重叠
						// 返回true
						if (this.x >= et.x && this.x <= et.x + 60
								&& this.y + 60 >= et.y
								&& this.y + 60 <= et.y + 40) {
							return true;
						}
						if (this.x + 40 >= et.x && this.x + 40 <= et.x + 60
								&& this.y + 60 >= et.y
								&& this.y + 60 <= et.y + 40) {
							return true;
						}
					}
				}
			}
			break;
		}
		case 3: {
			// 当前敌人坦克向左
			// 取出所有的敌人坦克
			for (int i = 0; i < ets.size(); i++) {
				// 取出第一个坦克
				EnemyTank et = ets.get(i);
				// 如果不是自己
				if (et != this) {
					// 如果敌人的方向是向下或者向上
					if (et.direct == 2 || et.direct == 0) {
						// 如果重叠
						// 返回true
						if (this.x >= et.x && this.x <= et.x + 40
								&& this.y >= et.y && this.y <= et.y + 60) {
							return true;
						}
						if (this.x >= et.x && this.x <= et.x + 40
								&& this.y + 40 >= et.y
								&& this.y + 40 <= et.y + 60) {
							return true;
						}
					}
					// 如果敌人的方向是向左或者向右
					if (et.direct == 3 || et.direct == 1) {
						// 如果重叠
						// 返回true
						if (this.x >= et.x && this.x <= et.x + 60
								&& this.y >= et.y && this.y <= et.y + 40) {
							return true;
						}
						if (this.x >= et.x && this.x <= et.x + 60
								&& this.y + 40 >= et.y
								&& this.y + 40 <= et.y + 40) {
							return true;
						}
					}
				}
			}
			break;
		}
		}
		return b;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (isMove) {
				switch (direct) {
				case 0: {
					// 坦克正在向上移动
					for (int i = 0; i < 30; i++) {
						if (y > 0 && !isTouchOtherEnemy()) {
							y -= speed;
							try {
								Thread.sleep(50);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					break;
				}
				case 1: {
					// 向右
					for (int i = 0; i < 30; i++) {
						if (x < 730 && !isTouchOtherEnemy()) {
							x += speed;
							try {
								Thread.sleep(50);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					break;
				}
				case 2: {
					// 向下
					for (int i = 0; i < 30; i++) {
						if (y < 500 && !isTouchOtherEnemy()) {
							y += speed;
							try {
								Thread.sleep(50);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					break;
				}
				case 3: {
					// 向左
					for (int i = 0; i < 30; i++) {
						if (x > 0 && !isTouchOtherEnemy()) {
							x -= speed;
							try {
								Thread.sleep(50);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					break;
				}
				}
			}

			// 敌人是否发射子弹
			if (isShot) {
				if (isLive) {
					// 判断子弹是否没有
					if (shots.size() < 5) {
						Shot s = null;
						// 没有子弹
						// 创建子弹
						switch (direct) {
						case 0: {
							s = new Shot(x + 20, y, 0);
							shots.add(s);
							break;
						}
						case 1: {
							s = new Shot(x + 60, y + 20, 1);
							shots.add(s);
							break;
						}
						case 2: {
							s = new Shot(x + 20, y + 60, 2);
							shots.add(s);
							break;
						}
						case 3: {
							s = new Shot(x, y + 20, 3);
							shots.add(s);
							break;
						}
						}

						// 启动子弹
						Thread t = new Thread(s);
						t.start();
					}
				}
			}

			if (isMove) {
				// 坦克随机产生一个新的方向
				this.direct = (int) (Math.random() * 4);
			}

			// 判断敌人的坦克是否已死亡
			if (isLive == false) {
				// 让坦克死亡后，退出线程
				break;
			}

		}
	}
}