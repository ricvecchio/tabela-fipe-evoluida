package br.com.alura.fipeveiculos.service;

public interface IConverteDados {
    <T> T  obterDados(String json, Class<T> classe);
}
