package com.crisnello.moviedb.entitie;

import java.io.Serializable;

/**
 * Created by crisnello
 */

public class Usuario implements Serializable{

    private static final long serialVersionUID = 8023901815942617149L;

//    private long id;

    private String id;

    private String nome;

    private String email;

    private String senha;

    private String cpf;

    private String cep;

    private String endereco;

    private long numero;

    private String complemento;

    private String bairro;

    private String uf;

    private String cidade;

    private String telefone;

//    private String celular;

    private String sexo;

    private String facul;

    private String data;

    private int idPerfil;

    private int idCliente;


    public Usuario(){

        setIdCliente(-1);

    }

    public Usuario(String nome,int idCliente){
        setNome(nome);
        setIdCliente(idCliente);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getFacul() {
        return facul;
    }

    public void setFacul(String facul) {
        this.facul = facul;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(int idPerfil) {
        this.idPerfil = idPerfil;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }



    @Override
    public String toString() {
        return getNome() + " - "+getEmail()+ " - "+getSenha();
    }



}
