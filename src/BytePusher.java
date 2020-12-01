import java.nio.file.*;
import java.io.*;
import java.util.*;

public class BytePusher
{
    int pc = 0;
    byte[] mem = new byte[0x1000008];
    int[] pal = new int[0x100];

    public BytePusher(String filename)
    {
		// read entire file into a buf
		Path p = FileSystems.getDefault().getPath("",filename);

		byte [] buf; 
		try
		{
			buf = Files.readAllBytes(p);
            for(int i = 0; i < buf.length; i++)
            {
                mem[i] = buf[i];
            }
		}

		catch(IOException e)
		{
			System.out.printf("failed to open file: %s\n",filename);
			System.exit(1);
		}  

        Arrays.fill(pal,0xff000000);

        // okay init websafe pallete 
        for(int r = 0; r < 6; r++)
        {
            for(int g = 0; g < 6; g++)
            {
                for(int b = 0; b < 6; b++)
                {
                    pal[r*36 + g * 6 + b] = (0xff << 24) | ((r * 33) << 16) | ((g * 33) << 8) | (b * 33);
                }
            }
        } 
    }


    public void run()
    {

        // first pc fetch of a frame is special
        pc = mem[2] << 16 | mem[3] << 8 | mem[4];

        for(int i = 0; i <= 65536; i++)
        {
            step();
        }
    }

    void step()
    {
        // A, B , C 24 bit addrs bit endian

        // 1 byte mem[B] = mem[A]
        final int a_addr = mem[pc+3] << 16 | mem[pc+4] << mem[pc+5];
        final byte a = mem[a_addr];
        final int b_addr = mem[pc] << 16 | mem[pc+1] << 8 | mem[pc+2];
        mem[b_addr] = a;

        // pc = mem[C];
        pc = mem[pc+6] << 16 | mem[pc+7] << 8 | mem[pc+8];    
    }

}