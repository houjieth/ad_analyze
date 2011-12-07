def trans(p):
    t = p[0].rstrip()
    s = p[1].rstrip()
    return (int(t),int(s))

timeF = open("time.txt")
sizeF = open("size.txt")
zipped = zip(timeF, sizeF)
mapped = map(trans, zipped)
startTime = mapped[0][0]
times = [mapped[0][0]]
sizes = [mapped[0][1]]
mapped = mapped[1:-1]

for p in mapped:
    while p[0] > times[-1]:
        times.append(times[-1] + 1 )
        sizes.append(0)
    sizes[-1] += p[1]

timeF.close()
sizeF.close()
timeF = open("time2.txt", "w")
sizeF = open("size2.txt", "w")

times = map((lambda x: x - startTime), times)

for time in times:
    timeF.write(" "+str(time))
for size in sizes:
    sizeF.write(" "+str(size))
sizeF.flush()
timeF.flush()    
sizeF.close()
timeF.close()
