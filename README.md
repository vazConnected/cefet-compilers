# Implementação de um Compilador

## Objetivo
O objetivo deste projeto é construir um compilador completo para uma dada linguagem de programação.

## Descrição Técnica
Esse projeto foi desenvolvido em [Java](https://download.oracle.com/java/20/latest/jdk-20_windows-x64_bin.exe) e foi segmentado em quatro etapas:

1. Análise Léxica e Tabela de Símbolos;
2. Análise Sintática
3. Análise Semântica
4. Geração do Código

Para cada etapa haverá um release específico contendo as descrições de uso e execução.

## Características da Linguagem
- As palavras-chave da linguagem são reservadas.
- Toda variável deve ser declarada antes do seu uso.
- A entrada e a saída da linguagem estão limitadas ao teclado e à tela do computador.
- A linguagem possui comentário de uma linha que começam com "//"
- A linguagem possui comentário de mais de uma linha que começam com "/*" e termina com "*/"
- O operador "+", quando aplicado a dado do tipo string, representa concatenação.
- Os demais operadores aritméticos são aplicáveis somente aos tipos numéricos.
- O resultado da divisão entre dois números inteiros é um número real.
- Somente tipos iguais são compatíveis nesta linguagem.
- As operações de comparação resultam em valor lógico (verdadeiro ou falso)
- Nos testes (dos comandos condicionais e de repetição) a expressão a ser validada deve ser um valor lógico.
- A semântica dos demais comandos e expressões é a tradicional de linguagens como Java e C.
- A linguagem é case-sensitive.
- O compilador da linguagem deverá gerar código a ser executado na máquina VM ou para Jasmin. VM é uma máquina virtual simples, porém possui a limitação de ser executada somente em Windows. O arquivo executável e a documentação de de VM estão disponíveis no Moodle. Jasmin é uma ferramenta que produz bytecodes a serem executados na JVM (Java Virtual Machine).

## Etapas do Compilador

### 1. Análise Léxica e Tabela de Símbolos;
O papel do analisador léxico é realizar a tokenização do arquivo fonte. Para isso, o seguinte padrão de gerador de tokens foi adotado:
```
- constant → integer_const | literal | real_const
- integer_const → nonzero digit* | 0
- real_const → interger_const "." digit+
- literal → " “ " caractere* " ” "
- identifier → letter {letter | digit | " _ " }
- letter → [A-Za-z]
- digit → [0-9]
- nonzero → [1-9]
- caractere → um dos 256 caracteres do conjunto ASCII, exceto as aspas e quebra de linha
```
Para além, tokens comparadores, lógicos e matemáticos, como ```<=```, ```&&``` e ```/```, devem ser identificados corretamente.

### 2. Análise Sintática
Inicialmente, a gramática da linguagem foi definida da seguinte forma:
```
- program ::= class identifier [decl-list] body
- decl-list ::= decl ";" { decl ";"}
- decl ::= type ident-list
- ident-list ::= identifier {"," identifier}
- type ::= int | string | float
- body ::= "{" stmt-list "}"
- stmt-list ::= stmt ";" { stmt ";" }
- stmt ::= assign-stmt | if-stmt | do-stmt | read-stmt | write-stmt
- assign-stmt ::= identifier "=" simple_expr
- if-stmt ::= if "(" condition ")" "{" stmt-list "}"  | if "(" condition ")" "{" stmt-list "}" else "{" stmt-list "}"
- condition ::= expression
- do-stmt ::= do "{" stmt-list "}" do-suffix
- do-suffix ::= while "(" condition ")"
- read-stmt ::= read "(" identifier ")"
- write-stmt ::= write "(" writable ")"
- writable ::= simple-expr
- expression ::= simple-expr | simple-expr relop simple-expr
- simple-expr ::= term | simple-expr addop term
- term ::= factor-a | term mulop factor-a
- factor-a ::= factor | "!" factor | "-" factor
- factor ::= identifier | constant | "(" expression ")"
- relop ::= ">" | ">=" | "<" | "<=" | "!=" | "=="
- addop ::= "+" | "-" | "||"
- mulop ::= "*" | "/" | "&&"
```
Entretanto, para desenvolver o parser recursivo descendente, é necessário transformar a gramática em LL(1). A seguir a gramática modificada:
```
program ::= class identifier [decl-list] body
decl-list ::= decl decl-list'
decl-list' ::= ";" decl decl-list'
  | ε
decl ::= type ident-list
ident-list ::= identifier ident-list'
ident-list' ::= "," identifier ident-list'
  | ε
type ::= int | string | float
body ::= "{" stmt-list "}"
stmt-list ::= stmt stmt-list'
stmt-list' ::= ";" stmt stmt-list' | ε
stmt ::= if "(" condition ")" "{" stmt-list "}" stmt-else
  | identifier "=" simple-expr
  | do "{" stmt-list "}" do-suffix
  | read "(" identifier ")"
  | write "(" writable ")"
stmt-else ::= else "{" stmt-list "}" | ε
assign-stmt ::= identifier "=" simple-expr
if-stmt ::= if "(" condition ")" "{" stmt-list "}" else "{" stmt-list "}"
  | if "(" condition ")" "{" stmt-list "}"
condition ::= expression
do-stmt ::= do "{" stmt-list "}" do-suffix
do-suffix ::= while "(" condition ")"
read-stmt ::= read "(" identifier ")"
write-stmt ::= write "(" writable ")"
writable ::= simple-expr
expression ::= simple-expr expression'
expression' ::= relop simple-expr expression' | ε
simple-expr ::= term simple-expr'
simple-expr' ::= addop term simple-expr' | ε
term ::= factor-a term'
term' ::= mulop factor-a term' | ε
factor-a ::= factor | "!" factor | "-" factor
factor ::= identifier | constant | "(" expression ")"
relop ::= ">" | ">=" | "<" | "<=" | "!=" | "=="
addop ::= "+" | "-" | "||"
mulop ::= "*" | "/" | "&&"
```

### 3. Análise Semântica
TODO

### 4. Geração do Código
TODO


## Releases
- Analisador Léxico e Tabela de Símbolos: [link do release](https://github.com/vazConnected/cefet-compilers/releases/tag/analisador_lexico)
- Analisador Sintático: WIP
- Analisador Semântico: TBA
- Gerador de Código: TBA

## Autores
Esse projeto foi desenvolvido para a disciplina de Compiladores pelos seguintes estudantes:
- [Alanis Castro](https://github.com/alaniscastro);
- [Pedro Vaz](https://github.com/vazConnected);
- [Stéphanie Fonseca](https://github.com/steponnie).
