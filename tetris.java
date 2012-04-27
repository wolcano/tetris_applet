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

	ArrayList<Kocka> kocky;

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
	int kociek_na_item = 1;
	int nasyp = 0;
	Color new_color;
	int status = 0; // stav behu 0 - STOP; 1 - RUN
	boolean suvisle_kocky = false;

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
		new_color = new Color(rand.nextInt(255),rand.nextInt(255), rand.nextInt(255));
		kocky = new ArrayList<Kocka>();
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

		for (int i = 0; i < w - 1; i++) {
			kocky.add(new Kocka(i, 0, 0, 0, new_color));
		}
		stepGame();
		//stepGame();
		//stopGame();
	}

	private void stopGame() {
		//start.setVisible(true);
		tick.stop();
		status = 2;
		repaint();
	}

	private boolean jePadajuca(Kocka k) {
		return (k != null && k.speed > 0);
	}

	private int pocetZijucichKociek() {
		int cnt = 0;

		for (Kocka k : kocky) {
			if (jePadajuca(k)) {
				cnt = cnt + 1;
			}
		}
		return cnt;
	}

	private boolean maPrazdno(Kocka pk, int dx, int dy) {
		if (pk.y + dy < 0
				|| pk.y + dy >= h
				|| pk.x + dx < 0
				|| pk.x + dx >= w) {
			return false;
		}
		for (Kocka k : kocky) {
			if ((pk.x + dx) == k.x
					&& (pk.y + dy) == k.y
					&& k.speed == 0)
				return false;
		}
		return true;
	}

	private void vybuchniRiadky() {
		int cnt;
		for (int i = 0; i < h; ) {
			cnt = 0;
			for (Kocka k : kocky) {
				if (k.y == i) {
					cnt++;
				}
			}
			if (cnt == w) {
				for (Kocka k : kocky) {
					if (k.y == i) {
						k.status = 2;
					}
				}
			}
			else {
				i++;
			}
		}
		// mame vsetky, nechame ich s efektom vybuchnut
		for (Kocka k : kocky) {
			if (k.status == 2) {
				kocky.remove(k);
			}
			else {
				k.speed = 1;
			}
		}
		while (padniKocky())
			;
	}

	private boolean posunPadajuce(int dx, int dy) {
		int cnt = 0;
		ArrayList<Kocka> padaju = new ArrayList<Kocka>();
		for (Kocka k : kocky) {
			if (jePadajuca(k)) {
				if (maPrazdno(k, dx, dy)) {
					padaju.add(k);
					cnt++;
				}
				else {
					return false;
				}
			}
		}

		for (Kocka k : padaju) {
			k.x += dx;
			k.y += dy;
		}

		return (cnt > 0);
	}

	private boolean rotujPadajuce() {
		return false;
	}

	private boolean padniKocky() {
		if (!posunPadajuce(0, -1)) {
			// ak nemohli padnut, tak ich zastav
			for (Kocka k : kocky) {
				if (k.speed > 0) {
					k.speed = 0;
				}
			}
			return false;
		}
		return true;
	}

	private int nasypNoveKocky(int speed) {
		int r = rand.nextInt(nasyp) + 1;
		nasyp -= r;

		int minx = -1;
		int maxx = -1;

		if (suvisle_kocky) {
			// najdem prvu a poslednu aktivnu kocku v -1 riadku
			// predpokladam, ze kocky uz o jeden riadok padli
			for (Kocka k : kocky) {
				if (k.y == h - 1 && k.speed > 0) {
					if (maxx == -1 || k.x > maxx) {
						maxx = k.x;
					}
					if (minx == -1 || k.x < minx) {
						minx = k.x;
					}
				}
			}
			if (minx == -1 || maxx == -1) {
				minx = w / 2;
				maxx = minx + 1;
			}
		}
		else {
			minx = 0;
			maxx = w - 1;
		}

		int rr = 0;
		if (maxx > minx) {
			rr += rand.nextInt(maxx - minx) + 1;
		}
		rr += minx - r + 1 + rand.nextInt(r);
		if (rr < 0) {
			rr = 0;
		}
		if (rr > w - r) {
			rr = w - r;
		}
		// najprv sa pozrieme, ci nam tam nezavadzia nejaka kocka
		for (int i = 0; i < r; i++) {
			for (Kocka k : kocky) {
				if (k.x == rr + i && k.y == h) {
					return 0;
				}
			}
		}
		// nasypavam r-suvisly blok kociek tak aby sa dotykal bloku [minx..maxx]
		for (int i = 0; i < r; i++) {
			kocky.add(new Kocka(rr + i, h, speed, 1, new_color));
		}

		return r;
	}

	private void stepGame() {
		int speed = 0;
		try {
			speed = Integer.parseInt(level.getText());
		}
		catch(Exception e) {
		}

		int zije = pocetZijucichKociek();
		if (zije == 0) {
			nasyp = kociek_na_item;
			new_color = new Color(rand.nextInt(255),rand.nextInt(255), rand.nextInt(255));
		}
		if (nasyp > 0) {
			if (nasypNoveKocky(speed) == 0) {
				// nepodarilo sa nasypat
				stopGame();
				return;
			}
		}
		if (!padniKocky()) {
			// padanie zastalo, skontroluj, ci nema nieco vybuchnut
			vybuchniRiadky();
		}

		repaint();
		//stopGame();
	}

	public void paint (Graphics gX)
	{
		Graphics2D g = (Graphics2D) gX;

		g.setColor (Color.red);
		g.draw(new Rectangle(cnv_x, cnv_y, cnv_w, cnv_h));

		for (Kocka k : kocky) {
			k.draw(g);
		}

		dbg.setText("tick! " + " k:" + pocetZijucichKociek() + "/" + kocky.size() + " " + debug);
	}

	public void keyReleased(KeyEvent evt) {
	}

	public void keyPressed(KeyEvent evt) {
	}

	public void keyTyped(KeyEvent evt) {
		kc = evt.getKeyChar();
		switch (kc) {
			case 'a':
				posunPadajuce(-1, 0);
				break;
			case 'd':
				posunPadajuce(1, 0);
				break;
			case 's':
				stepGame();
				break;
			case 'q':
				stopGame();
				break;
			case ' ':
				rotujPadajuce();
				break;
		}

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

	class Kocka {
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

		public Kocka() {
		}

		public Kocka(int px, int py, int pspeed, int pstatus, Color pcolor) {
			x = px;
			y = py;
			speed = pspeed;
			status = pstatus;
			color = pcolor;
		}
	}

}

