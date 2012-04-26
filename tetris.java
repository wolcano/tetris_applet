import java.awt.*;
import java.applet.*;
//import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.Timer;
import java.util.Random;
import java.util.ArrayList;

public class tetris extends Applet implements KeyListener, ActionListener
{

	static final long serialVersionUID = 1L;

	ArrayList<kocka> kocky;

	Label skore;
	Label skore_d;
	Label level;
	Label level_d;
	Button start;

	Label dbg;

	Timer tick;
	Random rand;

	int sq_w = 10; // velkost kocky
	int w = 10;
	int h = 10;
	int cnv_x = 140;
	int cnv_y = 40;
	int cnv_h = sq_w * h; // vyska hracej plochy
	int cnv_w = sq_w * w; // sirka hracej plochy

	int tick_len = 1000;
	int kociek_na_item = 4;
	int nasyp = 0;
	int status = 0; // stav behu 0 - STOP; 1 - RUN

	int q = 0;
	char kc = ' ';
	String debug;

	public void init()
	{
		setLayout(null);

		kocky = null;
		tick = new Timer(0, null);
		rand = new Random();
		debug = new String();

		dbg     = new Label("debug info");
		skore_d = new Label("SKORE");
		skore   = new Label();
		level_d = new Label("Level");
		level   = new Label();

		start   = new Button("START");

		dbg.setBounds(    20,  0, 800, 20);
		skore_d.setBounds(20, 20, 100, 20);
		skore.setBounds(  20, 40, 100, 20);
		level_d.setBounds(20, 60, 100, 20);
		level.setBounds(  20, 80, 100, 20);

		start.setBounds(cnv_x, cnv_y, cnv_w, cnv_h);

		add(dbg);
		add(skore_d);
		add(skore);
		add(level_d);
		add(level);
		add(start);

		start.addActionListener(this);
		tick.addActionListener(this);
		this.addKeyListener(this);

		reset();
	}

	private void reset() {
		start.setVisible(true);
		skore.setText("0");
		level.setText("5");
		tick.stop();
		kocky = new ArrayList<kocka>();
	}

	private void startGame() {
		start.setVisible(false);
		tick.stop();
		tick.setDelay(2 * tick_len / Integer.parseInt(level.getText()));
		tick.setInitialDelay(tick.getDelay());
		//tick.setDelay(tick_len);
		tick.start();
		status = 1;
		nasyp = kociek_na_item;
		debug = "startGame";
		stepGame();
	}

	private void stopGame() {
		start.setVisible(true);
		tick.stop();
		status = 0;
		debug = "stopGame";
		repaint();
	}

	private int pocetZijucichKociek() {
		int cnt = 0;

		for (kocka k : kocky) {
			if (k.speed > 0) {
				cnt = cnt + 1;
			}
		}
		return cnt;
	}

	private boolean maPrazdnoPodSebou(kocka pk) {
		if (pk.y <= 0) {
			return false;
		}
		for (kocka k : kocky) {
			if (pk.x == k.x && pk.y - 1 == k.y)
				return false;
		}
		return true;
	}

	private void vybuchniRiadky() {
		return;
		/*
		int cnt;
		for (int i = 0; i < h; ) {
			cnt = 0;
			for (kocka k : kocky) {
				if (k.y == i) {
					cnt++;
				}
			}
			if (cnt == w) {
				for (kocka k : kocky) {
					if (k.y == i) {
						k.status = 1;
					}
				}
			}
			else {
				i++;
			}
		}
		// mame vsetky, nechame ich s efektom vybuchnut
		for (kocka k : kocky) {
			if (k.status == 1) {
				kocky.remove(k);
			}
		}
		*/
	}

	private boolean padniKocky() {
		int zije = pocetZijucichKociek();
		ArrayList<kocka> padaju = new ArrayList<kocka>();

		for (kocka k : kocky) {
			if (maPrazdnoPodSebou(k)) {
				padaju.add(k);
			}
		}
		debug = zije + " " + padaju.size();

		// nemozu padnut vsetky
		if (zije > padaju.size()) {
			for (kocka k : kocky) {
				if (k.speed > 0) {
					k.speed = 0;
				}
			}
			vybuchniRiadky();

			return false;
		}

		for (kocka k : padaju) {
			k.y = k.y - 1;
		}
		return true;
	}

	private int nasypNoveKocky(int speed) {
		if (nasyp <= 0) {
			return 0;
		}
		int r = rand.nextInt(nasyp) + 1;
		Color c = new Color(rand.nextFloat(255),rand.nextFloat(255),rand.nextFloat(255));
		nasyp -= r;

		int minx = w / 2;
		int maxx = minx + 1;

		// najdem prvu a poslednu aktivnu kocku v -1 riadku
		// predpokladam, ze kocky uz o jeden riadok padli
		for (kocka k : kocky) {
			if (k.y == h - 1 && k.speed > 0) {
				if (k.x > maxx) {
					maxx = k.x;
				}
				if (k.x < minx) {
					minx = k.x;
				}
			}
		}

		kocka k;
		int rr = minx - r + 1 + rand.nextInt(maxx - minx) + 1;
		// nasypavam r-suvisly blok kociek tak aby sa dotykal bloku [minx..maxx]
		for (int i = 0; i < r; i++) {
			k = new kocka();
			k.x = rr + i;
			k.y = h - 1;
			k.color = c;
			k.speed = speed;
			kocky.add(k);
		}
		debug = "nasypNK "+rr+"+"+r+"_["+minx+","+maxx+"]";

		return r;
	}

	private void stepGame() {
		q = q + 1;
		int speed = 0;
		try {
			speed = Integer.parseInt(level.getText());
		}
		catch(Exception e) {
		}

		int zije = pocetZijucichKociek();
		if (zije == 0) {
			nasyp = kociek_na_item;
		}
		if (nasyp > 0) {
			nasypNoveKocky(speed);
		}
		if (!padniKocky()) {
			//stopGame();
		}
		//debug = "stepGame " + nasyp;

		repaint();
	}

	public void paint (Graphics gX)
	{
		Graphics2D g = (Graphics2D) gX;

		g.setColor (Color.red);
		g.draw(new Rectangle(cnv_x, cnv_y, cnv_w, cnv_h));

		for (kocka k : kocky) {
			k.draw(g);
		}

		dbg.setText("tick! " + q + " " + tick.getDelay() + " key:" + kc + " k:" + pocetZijucichKociek() + "/" + kocky.size() + " " + debug);
	}

	public void keyReleased(KeyEvent evt) {
	}

	public void keyPressed(KeyEvent evt) {
		keyTyped(evt);
	}

	public void keyTyped(KeyEvent evt) {
		kc = evt.getKeyChar();
		repaint();
	}

	// toto odchytava klikanie tlacitok
	public void actionPerformed(ActionEvent evt)
	{
		Object src = evt.getSource();

		if (src == start) {
			startGame();
		}
		else if (src == tick) {
			//tick.stop();
			stepGame();
			//tick.start();
		}
	}

	class kocka {
		int x;
		int y;
		int speed;
		int status;
		Color color;

		public void draw(Graphics2D g) {
			Rectangle r = new Rectangle(cnv_x + x*sq_w, cnv_y + (h - y - 1) * sq_w, sq_w, sq_w);
			g.setColor(color);
			g.fill(r);
			g.setColor(Color.black);
			g.draw(r);
		}
	}

}

