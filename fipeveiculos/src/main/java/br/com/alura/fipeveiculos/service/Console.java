package br.com.alura.fipeveiculos.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Console {

    public static String readString() { // leitura de strings
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler do teclado");
        }
    }

    public static int readInt() { // leitura de valores do tipo int (inteiros)
        String str = readString();
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            System.out.println(str + " não é válido");
            return 0;
        }
    }

    public static double readDouble() { // leitura de valores do tipo double (ponto flutuante)
        String str = readString();
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            System.out.println(str + " não é válido");
            return 0;
        }
    }
}



