for i in *.java; do
	gcj -C "$i" && p=`ps x | grep libnpjp2.so | grep -ve grep | sed -e 's,^ *,,' -e 's, .*,,'`;
	if [ ! "x$p" = "x" ]; then
		kill $p
	fi
done
