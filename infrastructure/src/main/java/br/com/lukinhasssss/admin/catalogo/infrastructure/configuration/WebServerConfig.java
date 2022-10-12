package br.com.lukinhasssss.admin.catalogo.infrastructure.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * WebServerConfig: Classe que será passada para o Spring configurar os Beans por padrão
 * @Configuration: Utilizada para o Spring entender que essa é uma classe de configuração
 *                 e que ele vai ler os métodos que estão aqui e entender como
 *                 Beans que ele vai gerenciar
 * @ComponentScan: Diz para o Spring, qual é o package padrão que ele vai examinar
 *                 para varrer classe por classe procurando as classes que tem as
 *                 anotações que ele usa para gerar um Bean
 */
@Configuration
@ComponentScan("br.com.lukinhasssss.admin.catalogo")
public class WebServerConfig {
}
