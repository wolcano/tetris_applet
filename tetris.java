import java.awt.*;
import java.applet.*;
//import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.Timer;
import java.util.Random;
import java.util.ArrayList;
import java.lang.Math;

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
	int h = 40;
	int cnv_x = 140;
	int cnv_y = 40;
	int cnv_h = sq_w * h; // vyska hracej plochy
	int cnv_w = sq_w * w; // sirka hracej plochy
	Color new_color;
	int nasyp = 0;
	int status = 0; // stav behu 0 - READY; 1 - RUN; 2 - FINISHED
	int riadkov = 0;

	int tick_len = 1000;
	int kociek_na_item = 4;
	boolean suvisle_kocky = true;
	int riadkov_na_level = 10;

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
		tick.stop();
		new_color = new Color(rand.nextInt(255),rand.nextInt(255), rand.nextInt(255));
		kocky = new ArrayList<Kocka>();
	}

	private void startGame() {
		tick.stop();
		start.setVisible(false);
		skore.setText("0");
		level.setText("1");
		tick.setDelay(2 * tick_len / Integer.parseInt(level.getText()));
		tick.setInitialDelay(tick.getDelay());
		//tick.setDelay(tick_len);
		tick.start();
		status = 1;
		nasyp = kociek_na_item;
		debug = "startGame";

		//* // XXX
		nasyp = 0;
		kocky.add(new Kocka(0, 0, 1, 0, new_color));
		kocky.add(new Kocka(1, 0, 1, 0, new_color));
		kocky.add(new Kocka(2, 0, 1, 0, new_color));
		kocky.add(new Kocka(3, 0, 1, 0, new_color));
		kocky.add(new Kocka(4, 0, 1, 0, new_color));
		kocky.add(new Kocka(6, 0, 1, 0, new_color));
		kocky.add(new Kocka(7, 0, 1, 0, new_color));
		kocky.add(new Kocka(8, 0, 1, 0, new_color));
		kocky.add(new Kocka(9, 0, 1, 0, new_color));
		//*/
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
	
	private boolean jeVolne(int x, int y) {
		if (y < 0 || y >= h || x < 0 || x >= w) {
			return false;
		}
		for (Kocka k : kocky) {
			if (x == k.x && y == k.y && k.speed == 0)
				return false;
		}
		return true;
	}

	private boolean maPrazdno(Kocka pk, int dx, int dy) {
		if (pk.y + dy < 0
				|| pk.y + dy >= h
				|| pk.x + dx < 0
				|| pk.x + dx >= w) {
			return false;
		}
		for (Kocka k : kocky) {
			if (!jeVolne(pk.x + dx, pk.y + dy))
				return false;
		}
		return true;
	}

	private void vybuchniRiadky() {
		int cnt;
		int minh = h;
		ArrayList<Kocka> zmazat = new ArrayList<Kocka>();
		for (int i = 0; i < h; i++) {
			cnt = 0;
			for (Kocka k : kocky) {
				if (k.y == i) {
					cnt++;
				}
			}
			if (cnt == w) {
				skore.setText(Integer.toString(Integer.parseInt(skore.getText()) + Integer.parseInt(level.getText())));
				riadkov++;
				if (riadkov > riadkov_na_level) {
					riadkov = 0;
					level.setText(Integer.toString(1 + Integer.parseInt(level.getText())));
				}
				if (i < minh) {
					minh = i;
				}
				for (Kocka k : kocky) {
					if (k.y == i) {
						zmazat.add(k);
					}
				}
			}
		}
		// mame riadky, nechame ich po jednom s efektom vybuchnut
		for (int i = minh; i < h; i++) {
			for (Kocka k : zmazat) {
				kocky.remove(k);
				repaint();
			}
			for (Kocka k : kocky) {
				if (k.y >= i) {
					k.speed = 1;
				}
			}
			cnt = 0;
			padniKocky();
			repaint();
		}
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
	private void rotujKocku(Kocka k, int x0, int y0) {
		double a = k.x - x0;
		double b = k.y - y0;
		k.x = (int) (+a *   0  - b * (-1) + x0);
		k.y = (int) (+a * (-1) + b *   0  + y0);
	}

	private boolean rotujPadajuce() {
		// zarotuje vsetky padajuce kocky v smere hodinovych ruciciek okolo
		// prvej aktivnej kocky v zozname (povodne najspodnejsia, najlavejsia)
		if (nasyp > 0) {
			return false;
		}
		ArrayList<Kocka> padaju = new ArrayList<Kocka>();
		for (Kocka k : kocky) {
			if (k.speed > 0) {
				padaju.add(k);
			}
		}
		if (padaju.size() == 0) {
			return false;
		}
		Kocka kk = padaju.get(0);
		int px = kk.x;
		int py = kk.y;
		int i = 0;
		kk = new Kocka();
		// najprv skontrolujem ci su volne vsetky pozicie
		for (Kocka k : padaju) {
			i++;
			kk.x = k.x;
			kk.y = k.y;
			rotujKocku(kk, px, py);
			if (!jeVolne(kk.x, kk.y)) {
				return false;
			}
		}
		for (Kocka k : padaju) {
			//kocky.add(new Kocka(k.x, k.y, 0, 0, Color.red));
			rotujKocku(k, px, py);
		}
		return true;
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
			if (nasyp == kociek_na_item) {
				new_color = new_color.brighter();
			}
			nasyp--;
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
		super.paint(gX);
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
		if (status != 1) {
			return;
		}
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
			case 'w':
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

