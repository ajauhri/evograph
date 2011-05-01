#!/usr/bin/env python
import sys

def main(max):
    for i in xrange(1, max+1):
        f = file("k"+str(i)+".rgf",'w')
        f.write(("%s \n") % i)
        for row in xrange(0,i):
            for column in xrange(row, i):
                f.write(("%d ") % column)
            f.write("\n")
        f.close()

if __name__ == "__main__":
    main(int(sys.argv[1]))
