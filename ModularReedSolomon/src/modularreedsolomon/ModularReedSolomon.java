/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modularreedsolomon;

import java.awt.Graphics;
import java.math.BigInteger;
import java.util.Scanner;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author leif
 */
public class ModularReedSolomon extends JComponent{
    static final int modulus=83;//THIS ABSOLUTELY HAS TO BE PRIME
    static final int A=2;//This ABSOLUTELY HAS TO BE a primitive root mod *modulus*
        //Examples: mod=251,A=6  mod=241,A=7
    static long time=Long.MAX_VALUE-2000;
    static int[] inv=new int[modulus];
    static char[] asdf={'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','0','1','2','3','4','5','6','7','8','9','.',',',':','?',';','!','(',')','=','+','-',' ','@','#','$','%','^','&','*','"','/'};
    public static Polynomial calcG(int A,int v){
        Polynomial G=new Polynomial(new int[] {1},modulus);
        for (int i=0; i<2*v; i++){
            int I=new BigInteger(A+"").modPow(new BigInteger((i+1)+""), new BigInteger(modulus+"")).intValue();
            G=G.multiply(new Polynomial(new int[] {modulus-I,1},modulus));
        }
        return G;
    }
    public static String decode(int v, int[] sss){
        Polynomial r=new Polynomial(sss,modulus);
        Polynomial s=null;
        for (int i=v; i>=1; i--){
            try{
                if (i==1){
            fixR(r,A,i,false);
                }else{
                    fixR(r,A,i,false);
                }
            System.out.println(i+" errors.");
            s=fixR(r,A,i,true);
            break;
            }catch(Exception e){
                System.out.println("Trying "+i+" errors: "+e);
            }
        }
        if (s!=null){
            Polynomial g=calcG(A,v);
            //int q=g.c.length;
            int q=1;
            int[] qq=new int[s.c.length-q+1];
            for (int i=0; i<qq.length; i++){
                qq[i]=s.c[i+q-1];
            }
            String result="";
            for (int i=qq.length-1; i>=0; i--){
                result+=(asdf[qq[i]]);
            }
            System.out.println(result);
            return result;
        }
        return "Error";
    }
    public static String encode(int v, int[] sss){
        Polynomial g=calcG(A,v);
            int[] qq=new int[g.c.length];
            for (int i=0; i<qq.length; i++){
                qq[i]=0;
            }
            qq[qq.length-1]=1;
            
            System.out.println("g:"+g);
            
        Polynomial p = new Polynomial(sss,modulus);
        Polynomial tempS=p.multiply(new Polynomial(qq,modulus));
        Polynomial tempSS=tempS.divide(g)[0];
        Polynomial s=tempS.subtract(tempSS);
        
        System.out.println("Your Message: "+p);
        System.out.println("Encoded message: "+s);
        return s.toString();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        System.out.println(asdf.length);
        for (int i=1; i<modulus; i++){
            inv[i]=MAYTRIX.invert(i,modulus);
        }
        Gooey gg=new Gooey();
        Scanner scan=new Scanner(System.in);
        System.out.print("Encode or decode? (e/d) >");
        
        String S=scan.nextLine();
        if (S.equals("d")){
            System.out.print("How many errors was the message able to correct when it was first encoded? This has to be exact. >");
        }else{
            System.out.print("How many errors should it be able to correct? >");
        }
        int v=Integer.parseInt(scan.nextLine());//Total errors that can be corrected
        System.out.println("Able to correct "+v+" errors, g polynomial has degree "+(2*v));
        
        
        
        if (S.equals("e")){
            System.out.print("Message? >");
            String[] ss=scan.nextLine().split(" ");
            int[] sss=new int[ss.length];
            for (int i=0; i<ss.length; i++){
                sss[i]=Integer.parseInt(ss[sss.length-i-1]);
            }
            encode(v,sss);
        return;
        }
        System.out.print("Encoded Message? >");
            String[] ss;ss = scan.nextLine().split(" ");
            time=System.currentTimeMillis();
            int[] sss=new int[ss.length];
            for (int i=0; i<ss.length; i++){
                sss[i]=Integer.parseInt(ss[sss.length-i-1]);
            }
        decode(v,sss);
        /*
            
            System.out.println("g:"+g);
        Polynomial[] om=s.divide(g);
        String Y=om[0].toString();
        if (Y.length()!=0){
            System.out.println("BIG ERROR "+Y);
        }
        System.out.println("Result: "+om[1]);
        M.repaint();
        if (S.equals("d")){
            System.out.print("Message? >");
            String[] ss;ss = scan.nextLine().split(" ");
            int[] sss=new int[ss.length];
            for (int i=0; i<ss.length; i++){
                sss[i]=Integer.parseInt(ss[sss.length-i-1]);
            }
            Polynomial g=calcG(A,v);
            System.out.println("g:"+g);
            Polynomial p=new Polynomial(sss,modulus);
            System.out.println(p.divide(g)[1]);
            
            System.out.println("Remainder, this really should be zero: "+p.divide(g)[0]);
            
        }*/
        //4System.out.println("Original: "+s);
    }
    public static Polynomial fixR(Polynomial r,int A,int v, boolean verbose) throws Exception{
        if (verbose){
        //System.out.println("Received (corrupted) message: "+r);
        //System.out.println();
        //System.out.println();
        System.out.println("Decoding...");
        //System.out.println();
        }
        int[] synd=new int[v*2];
        if (verbose)
        System.out.print("APow: ");
        for (int i=0; i<v*2; i++){
            BigInteger b=new BigInteger(A+"").pow(i+1);
            synd[i]=r.eval(b.mod(new BigInteger(modulus+"")).intValue());
            if (verbose)
            System.out.print(b+",");
        }
        if (verbose){
        System.out.println();
        System.out.print("Synd: ");
        for (int i : synd){
            System.out.print(i+",");
        }
        System.out.println();
        }
        
        //Calculating Sj square and rectancular matricies
        int[] q=new int[v];
        int[][] dat=new int[v][v];
        for (int i=0; i<v; i++){
            for (int j=0; j<v; j++){
                dat[i][j]=synd[i+j];
            }
            q[i]=(2*modulus-synd[i+v])%modulus;
        }
        //Inverting square matrix
        MAYTRIX m=new MAYTRIX(dat);
        MAYTRIX M=MAYTRIX.inverse(m,modulus);
        int[][] SS=M.data;
        if (dat.length==1){
            SS[0][0]=inv[dat[0][0]];
        }
        //Multiplying by rectangular to get Lambda coefficients
        int[] lambda=new int[v+1];//Note: In the wikipedia article, the upside down capital V is the lambda
        for (int i=0; i<v; i++){
            int sum=0;
            for (int j=0; j<v; j++){
                sum+=SS[i][j]*q[j];
                sum=sum%modulus;
            }
            lambda[v-i]=sum;
        }
        
        lambda[0]=1;
        //Making polynomial
        Polynomial Lambda=new Polynomial(lambda,modulus);
        if (verbose)
        System.out.println("Lambda: "+Lambda);
        
        //Finding roots of lambda by exhaustive search
        int index=0;
        int[] roots=new int[v];
        for (int i=0; i<modulus; i++){
            if (Lambda.eval(i)==0){
                roots[index++]=i;
            }
        }
        if (verbose){
        System.out.print("Roots: ");
        for (int i : roots){
            System.out.print(i+",");
        }
        }
        
        //Inverting roots to get Xk
        int[] invroots=new int[v];
        for (int i=0; i<v; i++){
invroots[i]=inv[roots[i]];            
}
        if (verbose){
        System.out.println();
        System.out.print("Unsorted invroots: ");
        for (int i : invroots){
            System.out.print(i+",");
        }
        System.out.println();
        }
        
        //BUBBLE SORT by power of A
        boolean done=false;
        while(!done){
            done=true;
            for (int i=0; i<invroots.length-1; i++){
                if (invert(A,invroots[i])>invert(A,invroots[i+1])){
                    int j=invroots[i];
                    invroots[i]=invroots[i+1];
                    invroots[i+1]=j;
                    done=false;
                }
            }
        }
        
        int[] Xk=invroots;
        if (verbose){
        System.out.print("Sorted invroots, Xk: ");
        for (int i : invroots){
            System.out.print(i+",");
        }
        System.out.println();
        }
        
        //Taking modular pseudoinverse of rectangular matrix, Xk
        BigInteger[][] XkMat=new BigInteger[synd.length][v];
        BigInteger[][] XkMatTransposed=new BigInteger[v][synd.length];
        if (verbose)
        System.out.print("XkMatrix: {{");
        for (int j=0; j<synd.length; j++){
            for (int i=0; i<v; i++){
                BigInteger Q=new BigInteger(Xk[i]+"").pow(j+1).mod(new BigInteger(""+modulus));
                XkMat[j][i]=Q;
                XkMatTransposed[i][j]=Q;
                if (verbose)
                System.out.print(Q.toString()+(i==v-1?"":","));
            }
            if (verbose)
            System.out.print(j==synd.length-1?"}}":"},{");
        }
        if (verbose)
        System.out.println("");
        int[][] temp=new int[v][v];
        for (int i=0; i<v; i++){
            for (int j=0; j<v; j++){
                BigInteger sum=BigInteger.ZERO;
                for (int n=0; n<synd.length; n++){
                    sum=sum.add(XkMatTransposed[i][n].multiply(XkMat[n][j]));
                }
                temp[i][j]=sum.mod(new BigInteger(modulus+"")).intValue();
            }
        }
        MAYTRIX MM=new MAYTRIX(temp);
        
        MAYTRIX MMM=MAYTRIX.inverse(MM,modulus);
        int[][] minv=MMM.data;
        if (temp.length==1){
            minv[0][0]=inv[temp[0][0]];
        }
        if (verbose)
        System.out.print("Inverted XkMatrix: {{");
        int[][] XkInv=new int[v][synd.length];
        for (int i=0; i<v; i++){
            for (int j=0; j<synd.length; j++){
                BigInteger sum=BigInteger.ZERO;
                for (int n=0; n<v; n++){
                    sum=sum.add(XkMatTransposed[n][j].multiply(new BigInteger(""+minv[n][i])));
                }
                XkInv[i][j]=sum.mod(new BigInteger(modulus+"")).intValue();
                if (verbose)
                System.out.print(XkInv[i][j]+(j==synd.length-1?"":","));
            }
            if (verbose)
            System.out.print(i==v-1?"}}":"},{");
        }
        
        //Multiplying inverted XkMat by Sj to get Yk
        System.out.println();
        int[] Yk=new int[v];
        for (int c=0; c<Yk.length; c++){
            int[] C=XkInv[c];
            int n=0;
            for (int i=0; i<C.length; i++){
                n+=(C[i]*synd[i])%modulus;
            }
            Yk[c]=n%modulus;
        }
        if (verbose){
        System.out.print("Yk: ");
        for (int i : Yk){
            System.out.print(i+",");
        }
        System.out.println();
        }
        //Calculating ik by getting the Ath roots of Xk
        int[] ik=new int[v];
        boolean[] ikDone=new boolean[v];
        for (int i=0; i<v; i++){
            ikDone[i]=false;
        }
        for (int j=0; j<modulus; j++){
            int w=new BigInteger(A+"").modPow(new BigInteger(j+""),new BigInteger(modulus+"")).intValue();
            for (int i=0; i<Xk.length; i++){
                if (Xk[i]==w && !ikDone[i]){
                    ik[i]=j;
                    ikDone[i]=true;
                    //System.out.println(i+","+j);
                }
            }
        }
        if (verbose){
            System.out.println();
            System.out.println();
        System.out.print("Error locations: ");
        for (int i : ik){
            System.out.print(i+",");
        }
        System.out.println();
        }
        
        //Calculating error polynomial
        Polynomial e=new Polynomial(new int[] {0},modulus);
        for (int i=0; i<v; i++){
            int[] Q=new int[ik[i]+1];
            Q[Q.length-1]=Yk[i];
            e=e.add(new Polynomial(Q,modulus));
        }
        Polynomial calcS=r.subtract(e);
        if (verbose){
        System.out.println("Calcualted Error:"+e);
        System.out.println("Decoding Finished.");
        System.out.println("Original (encoded) message  : "+calcS);
        }
        return calcS;
    }
    public static int invert(int A, int X){
        for (int j=0; j<modulus; j++){
            int w=new BigInteger(A+"").modPow(new BigInteger(j+""),new BigInteger(modulus+"")).intValue();
            if (X==w){
                return j;
            }
        }
        return 1;
    }
}
