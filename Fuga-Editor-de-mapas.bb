; Editor de mapas para o Fuga do Calabouço

; Constantes para uso no mapa
Const larguraDoMapa = 30 ; numero de tiles
Const alturaDoMapa  = 20 ; numero de tiles
Const larguraDoTile = 20
Const alturaDoTile  = 20
Const vazio  = 0
Const parede = 1
Const tileMovel = 2
Const tileFinal = 3  ; Posicao onde os tiles moveis devem chegar
Const tileInicio = 4 ; Posicao onde o jogador vai começar
Const tileChave  = 5 
Const tileTrancado = 6
Const tileMovelEspecial = 7
Const tileMovelEspecialFim = 8
Const tileChaveEscondida  = 9
Const quantidadeDeTiposDeTiles = 10

; Constantes para o teclado
Const setaCima     = 200
Const setaBaixo    = 208
Const setaEsquerda = 203
Const setaDireita  = 205
Const tecla_A = 30
Const tecla_Z = 44
Const F1 = 59
Const F2 = 60
Const tecla_espaco = 57

AppTitle "Fuga do Calabouço - Editor de mapas 1.0"
Graphics 800, 600, 16, 2
SeedRnd (MilliSecs())
SetBuffer BackBuffer()

; Carregando imagens dos blocos
Global blocoVerde = LoadImage("arqs/blocoVerde.png")

Global blocoParede = LoadImage("arqs/parede.png")
MaskImage blocoParede, 255, 0, 255
Global blocoVazio = LoadImage("arqs/vazio.png")
MaskImage blocoVazio, 255, 0, 255
Global blocoFinal = LoadImage("arqs/blocoFinal.png")
MaskImage blocoFinal, 255, 0, 255
Global blocoInicio = LoadImage("arqs/jogadorCima.png")
MaskImage blocoInicio, 255, 0, 255
Global blocoChave = LoadImage("arqs/chave.png")
MaskImage blocoChave, 255, 0, 255
Global blocoChaveEscondida = LoadImage("arqs/chaveEscondida.png")
MaskImage blocoChaveEscondida, 255, 0, 255
Global blocoTrancado = LoadImage("arqs/blocoTrancado.png")
MaskImage blocoTrancado, 255, 0, 255
Global blocoMovelEspecial = LoadImage("arqs/blocoMovelEspecial.png")
MaskImage blocoMovelEspecial, 255, 0, 255
Global blocoMovelEspecialFim = LoadImage("arqs/blocoMovelEspecialFim.png")
MaskImage blocoMovelEspecialFim, 255, 0, 255

Global tileSelecionado = 0 ; mude o tile selecionado usando A e Z

; Array que conterá todas as infos do mapa
Dim mapa(larguraDoMapa, alturaDoMapa)

Global tileSelecionadoX = 2
Global tileSelecionadoY = 2


; Colocando umas paredes em volta do mapa para facilitar a vida
For posicaoX = 1 To larguraDoMapa
	mapa(posicaoX, 1) = parede	
	mapa(posicaoX, alturaDoMapa) = parede
Next
For posicaoY = 1 To alturaDoMapa
	mapa(1, posicaoY) = parede	
	mapa(larguraDoMapa, posicaoY) = parede
Next

Function desenhaLinhas()
	Color 150,150,150

	For x = 1 To larguraDoMapa
		For y = 1 To alturaDoMapa
			Rect x*larguraDoTile, y*alturaDoTile, larguraDoTile, alturaDoTile, False
		Next
	Next
End Function

Function desenhaTileSelecionado()
	Color 0,250,0
	Rect tileSelecionadoX*larguraDoTile, tileSelecionadoY*alturaDoTile, larguraDoTile, alturaDoTile, False
End Function

Function moveTileSelecionado()
	If KeyHit(setaBaixo) And tileSelecionadoY < alturaDoMapa-1 Then tileSelecionadoY = tileSelecionadoY + 1
	If KeyHit(setaCima)  And tileSelecionadoY > 2 Then tileSelecionadoY = tileSelecionadoY - 1
	If KeyHit(setaEsquerda) And tileSelecionadoX > 2 Then tileSelecionadoX = tileSelecionadoX - 1
	If KeyHit(setaDireita)  And tileSelecionadoX < larguraDoMapa-1 Then tileSelecionadoX = tileSelecionadoX + 1
End Function

Function alteraTileSelecionado()
	; Aperte A para ir para o proximo tipo de tile (ou volta pro inicial)
	If KeyHit(tecla_A) Then tileSelecionado = (tileSelecionado + 1) Mod quantidadeDeTiposDeTiles
	; Aperte Z para voltar para o tile anterior ou ir para o ultimo (caso esteja no primeiro)
	If KeyHit(tecla_Z) Then 
		tileSelecionado =  tileSelecionado - 1
		If tileSelecionado < 0 Then tileSelecionado = quantidadeDeTiposDeTiles - 1
	EndIf
	
	If KeyDown(tecla_espaco) Then mapa(tileSelecionadoX, tileSelecionadoY) = tileSelecionado
End Function

Function saveMapa()
	Cls
	FlushKeys ; evitando "nnmm" bug
	Flip
	SetBuffer FrontBuffer()
	map_f$ = Input$ ("Salvar como : ")
	If Trim(map_f$) = "" Then 
		Print "Nome de arquivo inválido ! Preciona uma tecla para voltar ao editor."
		WaitKey()
	EndIf
	FlushKeys ; evitando "2x" bug 
	
	; Salvando ...
	If Trim(map_f$) <> ""
		fileout = WriteFile ( "arqs/" + map_f$ + ".ipm" )
		
		; Arquivando informações do mapa
		For x = 1 To larguraDoMapa
			For y = 1 To alturaDoMapa
				temp_s$ = Mapa (x,y)
				WriteLine ( fileout, temp_s$)	; Escreve info para o arquivo
			Next
		Next
		
		CloseFile(fileout)
		
		Print "Mapa " + map_f$ + ".ipm foi salvo com sucesso."
		WaitKey()
	EndIf	
	SetBuffer BackBuffer()		
End Function

Function loadMapa()
	Cls
	FlushKeys ; Evitando "nnmm" bug
	Flip
	SetBuffer FrontBuffer()
	
	map_f$ = Input$ ( "Arquivo a ser carregado : " )
	map_f$ = "arqs/" + map_f$ + ".ipm"
	Print "Carregando... aguarde..."
		
	file = ReadFile(map_f$) ; Abrindo para leitura
		If file<>0 		       ; Se existir :
			For x = 1 To larguraDoMapa
				For y = 1 To alturaDoMapa
					temp_s$ = ReadLine(file)
					mapa(x,y) = temp_s$
				Next
			Next
		
			
			CloseFile(file)

		Else 
			Print map_f$ + " -> Arquivo não encontrado ! Aperte qualquer tecla para voltar ao editor."
			WaitKey()				
		EndIf
	
	SetBuffer BackBuffer() 
	FlushKeys ; Evitando "2x" bug 	
End Function

Function printText()
	Color 255,255,255
	Text 50,450, "Textura selecionado : "
	Text 1,500,"A- Próximo textura    Z- Textura anterior   Espaço- Aplica textura"
	Text 1,520,"F1- Salva mapa    F2- Carrega mapa"
	Text 1,540,"Use as setas para mover o tile selecionado"
End Function

; ------ FUNCOES RETIRADAS DO JOGO INSANEPUZZLE
; Caso altere aqui, altere no jogo tambem, e vice-versa (o printTile tem umas diferenças.. tome cuidado)
Function printMap()
	For x = 1 To larguraDoMapa
		For y = 1 To alturaDoMapa
			printTile(x,y)
		Next
	Next
End Function

Function printTileSelecionado()
imagem = blocoVazio
	Select tileSelecionado
		Case parede
			imagem = blocoParede
		Case tileMovel
			imagem = blocoVerde
		Case tileFinal
			imagem = blocoFinal
		Case tileChave
			imagem = blocoChave
		Case tileChaveEscondida
			imagem = blocoChaveEscondida			
		Case tileTrancado
			imagem = blocoTrancado
		Case tileMovelEspecial
			imagem = blocoMovelEspecial
		Case tileMovelEspecialFim
			imagem = blocoMovelEspecialFim
						
		Case tileInicio
			imagem = blocoInicio
						
	End Select		
	
	DrawImage imagem, 230,450
End Function

Function printTile(posicaoX, posicaoY)
	; Vamos ver que tipo de tile que é, e de acordo com o tipo mudandos o desenho
	posicaoRealX = (posicaoX * larguraDoTile)
	posicaoRealY = (posicaoY * alturaDoTile)	
		
	Select mapa(posicaoX, posicaoY)

		Case parede
			DrawImage blocoParede, posicaoRealX , posicaoRealY
		Case vazio
			DrawImage blocoVazio, posicaoRealX , posicaoRealY
		Case tileMovel
			DrawImage blocoVerde, posicaoRealX , posicaoRealY
		Case tileFinal
			DrawImage blocoVazio, posicaoRealX , posicaoRealY
			DrawImage blocoFinal, posicaoRealX , posicaoRealY
		Case tileChave
			DrawImage blocoVazio, posicaoRealX , posicaoRealY
			DrawImage blocoChave, posicaoRealX , posicaoRealY
		Case tileTrancado
			DrawImage blocoTrancado, posicaoRealX , posicaoRealY
		Case tileMovelEspecial
			DrawImage blocoMovelEspecial, posicaoRealX , posicaoRealY
		Case tileMovelEspecialFim
			DrawImage blocoMovelEspecialFim, posicaoRealX , posicaoRealY
		Case tileChaveEscondida
			DrawImage blocoVazio, posicaoRealX , posicaoRealY
			DrawImage blocoChaveEscondida, posicaoRealX , posicaoRealY
		Case tileInicio
			DrawImage blocoVazio, posicaoRealX , posicaoRealY
			DrawImage blocoInicio, posicaoRealX , posicaoRealY
						
	End Select		
	
End Function
; -------------

While Not KeyHit(1)
	Cls
	
	printMap()
	printText()
	printTileSelecionado()
	alteraTileSelecionado()
	moveTileSelecionado()
	;desenhaLinhas()
	desenhaTileSelecionado()
	
	If KeyHit(F1) Then saveMapa()
	If KeyHit(F2) Then loadMapa()
	
	Flip()	
Wend