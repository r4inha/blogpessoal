package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)//utilizar portas automaticamente
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@BeforeAll //antes de tudo:
	void start() {
		usuarioRepository.deleteAll();
		
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Root", "root@root.com", "rootroot", "-"));//autenticação do teste
	}
	
	@Test
	@DisplayName("😁 Deve Cadastrar um novo usuário")
	public void deveCriarUmUsuario() {
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,
				"Paulo_Antunes", "paulo_antunes@email.com.br", "12345678", "-"));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());// se corpoRespota foi criado, mostrar status
	}
	
	@Test
	@DisplayName("😁 Não Deve permitir usuário duplicado")
	public void naoDeveDuplicarUsuario() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Maria", "maria@email.com.br", "12345678", "-"));//cadastrar user
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,
				"Maria", "maria@email.com.br", "12345678", "-"));//cadastrar user
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());// se corpoRespota já foi criado, mostrar status
	}
	
	@Test
	@DisplayName("😁 Deve permitir Atualizar usuário")
	public void deveAtualizarUmUsuario() {
		
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L,
				"juliana", "juliana@email.com.br", "12345678", "-"));//cadastrar user
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(usuarioCadastrado.get().getId(),
				"Juliana", "juliana@email.com.br", "12345678", "-"));//cadastrar user
		//buscar id por usuarioCadastrado
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());// se corpoRespota foi atualizado, mostrar status
	}
	
	@Test
	@DisplayName("😁 Deve listar todos os usuários")
	public void deveListarTodosUsuarios() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Maria", "maria@email.com.br", "12345678", "-"));//cadastrar user

		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Maria", "maria@email.com.br", "12345678", "-"));//cadastrar user
		
		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());// se corpoRespota foi atualizado, mostrar status
	}
	
	
	@Test
	@DisplayName("😁 Deve listar usuário por id")
	public void deveListarUmUsuarioPorId() {
		
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L,
				"ana", "ana@email.com.br", "12345678", "-"));//cadastrar user
		
		Long usuario = usuarioCadastrado.get().getId();

			
		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/" + usuario, HttpMethod.GET, null, String.class);
		
			assertEquals(HttpStatus.OK, resposta.getStatusCode());// se resposta existe, mostrar status
	}
	
	@Test
	@DisplayName("😁 Deve autenticar o login")
	public void deveAutenticarlogin() {
		
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L,
				"Joao", "joao@email.com.br", "12345678", "-"));//cadastrar user
		
		//importar a model UsuarioLogin
		HttpEntity<UsuarioLogin> loginUser = new HttpEntity<UsuarioLogin>(new UsuarioLogin(0L,
				"", "joao@email.com.br", "12345678", "-", ""));//cadastrar user
			
		ResponseEntity<UsuarioLogin> resposta = testRestTemplate
				.exchange("/usuarios/logar", HttpMethod.POST, loginUser, UsuarioLogin.class);
			assertEquals(HttpStatus.OK, resposta.getStatusCode());// se autenticacao teve exito, mostrar status
	}
	
	
	
	
	
}