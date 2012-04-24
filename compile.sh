for i in *.java; do
	gcj -C "$i"
done
