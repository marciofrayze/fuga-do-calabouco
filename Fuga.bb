; Fuga do Calabouço (ex Insane Puzzle)
; Inicio : 17/12/2003

; This game is Copyleft. You can use and change the code, as long as you respect the GPL license.
; Authors : Marcio Frayze David (mfdavid@gmail.com)
;					  Marcelo Baccelli (marcelo.baccelli@gmail.com)
; Insane Games - www.insanegames.tk (BR portuguese only)
; --
; Esse jogo é Copyleft. Você pode usar e alterar o código, desde que respeite a licença GPL.
; Autores : Marcio Frayze David (mfdavid@estadao.com.br)
;		    Marcelo Baccelli (coxinha2000@uol.com.br)
; Insane Games - www.insanegames.tk


; oq falta fazer :
; - Bug da chave escondida com blocos moveis

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
Const tileTrancado  = 6
Const tileMovelEspecial = 7
Const tileMovelEspecialFim = 8
Const tileChaveEscondida  = 9 ; Chave(s) que só aparece(m) quando os tiles especiais tiverem nos lugares corretos
Const tileMovelSobreTileEspecialMovelFim = 10 ; quando o idiota coloca um tile normal em cima do TileMovelEspecialFim
Const tileMovelEspecialSobreTileMovelEspecialFim = 11 ; tile final no fim.. =/

; Constantes para o teclado
Const setaCima     = 200
Const setaBaixo    = 208
Const setaEsquerda = 203
Const setaDireita  = 205
Const teclaR  = 19
Const teclaEnter = 28
Const teclaL = 38
Const ESC = 1

; Constantes para controlar o estado do jogo
Const jogando = 1		  ; Jogo normal
Const fimDoLevel = 2	  ; Chegou no fim do level
Const iniciandoLevel = 3  ; Aparecendo efeito "Level X"
Const iniciandoLevel2 = 4 ; Aparecendo o mapa
Const fimDoJogo = 5 ; Player completou todos os leveis (aparece tela final)

; Outras constantes
Const numeroDeLeveis = 18 ; numero total de leveis

AppTitle "Fuga do Calabouço 1.0"
Graphics3D 800, 600, 16, 1
SeedRnd (MilliSecs())
SetBuffer BackBuffer()
camera = CreateCamera()
CameraClsMode camera,0,1 ; 2D sobre 3D (fade)

Global estado = iniciandoLevel
Global movimentos = 0
Global level = 1
Global levelSave = 1 ; Qual o ultimo nivel que o jogador chegou

; Constantes para mover de forma mais suave
Const parado = 0
Const direita = 1
Const esquerda = 2
Const cima = 3
Const baixo = 4

Print ""
Print "FUGA DO CALABOUÇO"
Print "Copyleft, Insane Games - 2004"
Print "www.insanegames.tk"
Print ""
Print ""

; Informações sobre o jogador
Print "Carregando imagens..."
Global jogadorPosicao = cima
Dim jogador(4)
jogador(direita) = LoadImage("arqs\jogadorDireita.png")
jogador(esquerda) = LoadImage("arqs\jogadorEsquerda.png")
jogador(cima) = LoadImage("arqs\jogadorCima.png")
jogador(baixo) = LoadImage("arqs\jogadorBaixo.png")
MaskImage jogador(direita), 255, 0, 255
MaskImage jogador(esquerda), 255, 0, 255
MaskImage jogador(cima), 255, 0, 255
MaskImage jogador(baixo), 255, 0, 255
Global jogadorX = 3
Global jogadorY = 3
Global numeroDeChaves = 0 ; ele ainda nao possui a chave
Global mostrarChavesEscondidas = False ; vira true quando ele colocar os tiles especiais nos lugares corretos
; vars para mover de forma mais suave (ao inves do antigo tele-porte)
Global movendo = parado
Global addX# = 0
Global addY# = 0

Global level_introY# = 600

; Carregando imagens dos blocos
Global blocoVerde = LoadImage("arqs\blocoVerde.png")
MaskImage blocoVerde, 255, 0, 255
Global blocoParede = LoadImage("arqs\parede.png")
MaskImage blocoParede, 255, 0, 255
Global blocoVazio = LoadImage("arqs\vazio.png")
MaskImage blocoVazio, 255, 0, 255
Global blocoFinal = LoadImage("arqs\blocoFinal.png")
MaskImage blocoFinal, 255, 0, 255
Global blocoChave = LoadImage("arqs\chave.png")
MaskImage blocoChave, 255, 0, 255
;Global blocoChaveEscondida = LoadImage("arqs\chaveEscondida.png") ; Usado apenas no editor
;MaskImage blocoChaveEscondida, 255, 0, 255
Global blocoTrancado = LoadImage("arqs\blocoTrancado.png")
MaskImage blocoTrancado, 255, 0, 255
Global blocoMovelEspecial = LoadImage("arqs\blocoMovelEspecial.png")
MaskImage blocoMovelEspecial, 255, 0, 255
Global blocoMovelEspecialFim = LoadImage("arqs\blocoMovelEspecialFim.png")
MaskImage blocoMovelEspecialFim, 255, 0, 255

; Imagem(ns?) de fundo
Global fundo = LoadImage("arqs\fundo.png")
Global imgEntrada=LoadImage("arqs\entradaFundo.png")
Global imgEntrada2=LoadImage("arqs\entradaFundo2.png")

; Carregando sons
Print "Carregando sons..."
Global acessoNegado = LoadSound("arqs/acessoNegado.wav")
Global pegouChave = LoadSound("arqs/pegouChave.wav")
Global blocoLiberado = LoadSound("arqs/blocoLiberado.wav")
Global levelTerminado = LoadSound("arqs/levelTerminado.wav")
;Global levelTerminado1 = LoadSound("arqs/levelTerminado1.wav")
;Global levelTerminado2 = LoadSound("arqs/levelTerminado2.wav")
;Global jogadorMovendo = LoadSound("arqs/jogadorMovendo.wav")
Global blocoMovendo = LoadSound("arqs/blocoMovendo.wav")
Global chavesLiberada = LoadSound("arqs/chaveLiberada.wav")
Global channelAcessoNegado ; usado para evitar bug de segurar a tecla em direcao ao bloco trancado

; Carregando musicas
Print "Verificando musicas..."
Global numeroDeMusicas = 0
Global musica ; usada na tocarMusica
; Fazendo contagem do numero de musicas disponiveis
; Abrindo diretorio de musicas
myDir=ReadDir("musicas")
temp = 0
; Fazendo looping até nao ter mais arquivos restantes
Repeat
	file$=NextFile$(myDir) ; Proximo arquivo
	If file$="" Then Exit    ; Se nao tiver mais nenhum, tchau !
	If FileType("musicas"+file$) = 0  Then ; Verifica se é um arquivo (poderia ser diretorio)
		numeroDeMusicas = numeroDeMusicas + 1
	EndIf
Forever
CloseDir myDir

If numeroDeMusicas > 3 Then
	Print "Infelizmente no momento o sistema suporta no máximo 3 músicas. As demais serão ignoradas."
	numeroDeMusicas = 3
EndIf

; Re-lendo as musicas, e dessa vez carregando-as e colocando os handles em um array
Print "Carregando musicas..."
Dim musicas(numeroDeMusicas)
myDir=ReadDir("musicas")
temp = 0
; Fazendo looping até nao ter mais arquivos restantes
Repeat
	file$=NextFile$(myDir) ; Proximo arquivo

	If file$="" Or temp=3 Then Exit    ; Se nao tiver mais nenhum, tchau !
	If FileType("musicas"+file$) = 0  Then ; Verifica se é um arquivo (poderia ser diretorio)
		Print "Carregando " + file$
		musicas(temp) = LoadSound("musicas/" + file$)
		temp = temp + 1
	EndIf
Forever
CloseDir myDir

; Gerando fontes
Global Font1 = LoadFont("Arial",40)
Global Font2 = LoadFont("Arial",30,True)
Global Font3 = LoadFont("Arial",14,True)
Global Font4 = LoadFont("Arial",18,True)
SetFont Font1


; Array que conterá todas as infos do mapa
Dim mapa(larguraDoMapa, alturaDoMapa)

Function printTile(posicaoX, posicaoY, posicaoYdoMapa#=0)
	; Vamos ver que tipo de tile que é, e de acordo com o tipo mudandos o desenho
	posicaoRealX = (posicaoX * larguraDoTile) + 80
	posicaoRealY = (posicaoY * alturaDoTile) + 62 + posicaoYdoMapa#

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
		Case tileChaveEscondida
			DrawImage blocoVazio, posicaoRealX , posicaoRealY
			If mostrarChavesEscondidas = True Then DrawImage blocoChave, posicaoRealX , posicaoRealY
		Case tileTrancado
			DrawImage blocoTrancado, posicaoRealX , posicaoRealY
		Case tileMovelEspecial
			DrawImage blocoMovelEspecial, posicaoRealX , posicaoRealY
		Case tileMovelEspecialFim
			DrawImage blocoMovelEspecialFim, posicaoRealX , posicaoRealY
		Case tileMovelSobreTileEspecialMovelFim
			DrawImage blocoVerde, posicaoRealX , posicaoRealY
		Case tileMovelEspecialSobreTileMovelEspecialFim
			DrawImage blocoMovelEspecial, posicaoRealX , posicaoRealY

		Case tileInicio ; só é usado pelo editor de mapas, no jogo mostra como sendo vazio
			DrawImage blocoVazio, posicaoRealX , posicaoRealY

	End Select

End Function

Function teclado()
	If Not(KeyDown(teclaR)) Then ; evita bug de segurar o R
		moveu = False ; usado para evitar que o som fique mais alto ao tentar mover na diagonal
		If KeyDown(setaBaixo) Then
			moverPara(jogadorX, jogadorY+1, jogadorX, jogadorY+2, baixo)
			jogadorPosicao = baixo
			moveu = True
		EndIf
		If KeyDown(setaCima) And moveu=False Then
			moverPara(jogadorX, jogadorY-1, jogadorX, jogadorY-2, cima)
			jogadorPosicao = cima
			moveu = True
		EndIf
		If KeyDown(setaEsquerda) And moveu=False Then
			moverPara(jogadorX-1, jogadorY, jogadorX-2, jogadorY, esquerda)
			jogadorPosicao = esquerda
			moveu = True
		EndIf
		If KeyDown(setaDireita) And moveu=False Then
			moverPara(jogadorX+1, jogadorY, jogadorX+2, jogadorY, direita)
			jogadorPosicao = direita
			moveu=True
		EndIf
	EndIf

	If KeyDown(teclaR) Then loadMap(level + ".ipm")
End Function



Function loadMap(arquivo$)
	zeraVariaveis()

	file = ReadFile("arqs/" + arquivo$) ; Abrindo arquivo para leitura
	If file<>0 		       ; se o arquivo existir :
		For posX = 1 To larguraDoMapa
			For posY  = 1 To alturaDoMapa
				temp_s$ = ReadLine(file)
				mapa(posX, posY) = temp_s$

				; Verificando se é a posicao inicial do jogador
				If mapa(posX, posY) = tileInicio Then
					jogadorX = posX
					jogadorY = posY
					mapa(posX, posY) = vazio ; tratamos essa posicao como uma posicao vazia normal
				EndIf
			Next
		Next
	EndIf
	CloseFile(file)

End Function

; posicaoX, posicaoY = posicao pra onde ta indo
; posicaoX, posicaoY = 2 posicoes adiante da direcao pra onde ta indo
Function moverPara(posicaoX, posicaoY, posicaoX2, posicaoY2, direcao)
	Select mapa(posicaoX, posicaoY)
		Case vazio
			movendo = direcao
			movimentos = movimentos + 1
;			PlaySound (jogadorMovendo)
		Case tileMovel
			Select mapa(posicaoX2, posicaoY2)
				; Se nao tiver um tile emdirecao do tile que ele ta tentando empurrar, empurre o tile !
				Case vazio
					mapa(posicaoX, posicaoY) = vazio
					mapa(posicaoX2, posicaoY2) = tileMovel
					movendo =  direcao
					movimentos = movimentos + 1
					PlaySound blocoMovendo
				Case tileMovelEspecialFim
					mapa(posicaoX, posicaoY) = vazio
					mapa(posicaoX2, posicaoY2) = tileMovelSobreTileEspecialMovelFim
					movendo = direcao
					movimentos = movimentos + 1
					PlaySound blocoMovendo
			End Select
		Case tileMovelSobreTileEspecialMovelFim
			Select mapa(posicaoX2, posicaoY2)
				; Se nao tiver um tile emdirecao do tile que ele ta tentando empurrar, empurre o tile !
				Case vazio
					mapa(posicaoX, posicaoY) = tileMovelEspecialFim
					mapa(posicaoX2, posicaoY2) = tileMovel
					movendo =  direcao
					movimentos = movimentos + 1
					PlaySound blocoMovendo
				Case tileMovelEspecialFim
					mapa(posicaoX, posicaoY) = tileMovelEspecialFim
					mapa(posicaoX2, posicaoY2) = tileMovelSobreTileEspecialMovelFim
					movendo = direcao
					movimentos = movimentos + 1
					PlaySound blocoMovendo
			End Select
		Case tileFinal
			movendo = direcao
			movimentos = movimentos + 1
		Case tileChave
			movendo = direcao
			mapa(posicaoX, posicaoY) = vazio
			numeroDeChaves = numeroDeChaves + 1
			movimentos = movimentos + 1
			PlaySound pegouChave
		Case tileChaveEscondida
			If mostrarChavesEscondidas = True Then
				movendo = direcao
;				mapa(posicaoX, posicaoY) = vazio
				numeroDeChaves = numeroDeChaves + 1
				movimentos = movimentos + 1
				PlaySound pegouChave
			Else
				movendo = direcao
				movimentos = movimentos + 1
;				PlaySound (jogadorMovendo)
			EndIf
		Case tileTrancado
			If numeroDeChaves > 0 Then
				movendo =  direcao
;				mapa(jogadorX, jogadorY) = vazio
				numeroDeChaves = numeroDeChaves - 1
				movimentos = movimentos + 1
				PlaySound blocoLiberado
			Else
				If Not ChannelPlaying(channelacessoNegado) Then channelacessoNegado = PlaySound (acessoNegado)
			EndIf
		Case tileMovelEspecialFim
			movendo =  direcao
			movimentos = movimentos + 1
;			PlaySound (jogadorMovendo)
		Case tileMovelEspecial
			Select mapa(posicaoX2, posicaoY2)
				Case vazio
					mapa(posicaoX, posicaoY) = vazio
					mapa(posicaoX2, posicaoY2) = tileMovelEspecial
					movendo =  direcao
					movimentos = movimentos + 1
					PlaySound blocoMovendo
				Case tileMovelEspecialFim
					mapa(posicaoX, posicaoY) = vazio
					mapa(posicaoX2, posicaoY2) = tileMovelEspecialSobreTileMovelEspecialFim
					movendo = direcao
					movimentos = movimentos + 1
					PlaySound blocoMovendo
					verificaTilesEspeciais()
		End Select
		Case tileMovelEspecialSobreTileMovelEspecialFim
			Select mapa(posicaoX2, posicaoY2)
				Case vazio
					mapa(posicaoX, posicaoY) = tileMovelEspecialFim
					mapa(posicaoX2, posicaoY2) = tileMovelEspecial
					movendo =  direcao
					movimentos = movimentos + 1
					PlaySound blocoMovendo
				Case tileMovelEspecialFim
					mapa(posicaoX, posicaoY) = tileMovelEspecialFim
					mapa(posicaoX2, posicaoY2) = tileMovelEspecialSobreTileMovelEspecialFim
					movendo = direcao
					movimentos = movimentos + 1
					PlaySound blocoMovendo
					verificaTilesEspeciais()
		End Select

	End Select
End Function


Function printMap(posicaoYdoMapa#=0, desenharJogador=True)
	DrawImage fundo, 0, 0

	For x = 1 To larguraDoMapa
		For y = 1 To alturaDoMapa
			printTile(x,y,posicaoYdoMapa#)
		Next
	Next

	If desenharJogador Then DrawImage jogador(jogadorPosicao), (jogadorX * larguraDoTile) + 80 + addX#, (jogadorY * alturaDoTile) + 62 + addY#
End Function

Function printText()
	Color 255,255,255
;	Select estado
;		Case fimDoLevel
;			Text 1,550,"Fim do level " + level + " ! Aperte alguma tecla para ir para o próximo level."
;	End Select

	Text 143,6, level
	Text 369,44, movimentos
	Text 546,4, numeroDeChaves
End Function

Function LevelTerminado()
	PlaySound levelTerminado
;	Select Rand(2)
;		Case 1
;			PlaySound levelTerminado1
;		Case 2
;			PlaySound levelTerminado2
;	End Select
End Function

Function verificaTile()
	Select mapa(jogadorX, jogadorY)
		Case tileFinal
			estado = fimDoLevel
			levelTerminado()
		Case tileChaveEscondida
			If mostrarChavesEscondidas Then mapa(jogadorX, jogadorY) = vazio

		Case tileChave
			mapa(jogadorX, jogadorY) = vazio
		Case tileTrancado
			mapa(jogadorX, jogadorY) = vazio
	End Select
End Function

; Move o jogador de forma suave
Function movendoJogador()
	Select movendo
		Case direita
			addX# = addX# + 1.8	; movendo para o tile a direita
			If addX# >= 20 Then ; chegou no proximo tile
				addX# = 0
				jogadorX = jogadorX + 1
				movendo = parado
				verificaTile()
			EndIf
		Case esquerda
			addX# = addX# - 1.8
			If addX# <= -20 Then
				addX# = 0
				jogadorX = jogadorX - 1
				movendo = parado
				verificaTile()
			EndIf
		Case cima
			addY# = addY# - 1.8
			If addY# <= -20 Then
				addY# = 0
				jogadorY = jogadorY - 1
				movendo = parado
				verificaTile()
			EndIf
		Case baixo
			addY# = addY# + 1.8
			If addY# >= 20 Then
				addY# = 0
				jogadorY = jogadorY + 1
				movendo = parado
				verificaTile()
			EndIf
	End Select
End Function

Function zeraVariaveis()
	numeroDeChaves = 0
	movimentos = 0
	addX# = 0
	addY# = 0
	movendo = parado
	mostrarChavesEscondidas = False
	level_introY# = 600
End Function


; Verifica se ainda tem algum tileMovelEspecialFinal livre, se nao tiver, mostra as chaves escondidas
Function verificaTilesEspeciais()
	If mostrarChavesEscondidas = False Then
		mostrarChavesEscondidas = True
		For x = 1 To larguraDoMapa
			For y = 1 To alturaDoMapa
				; se ainda tiver algum tileMovelEspecialFim, entao nao mostre as chaves escondidas
				If mapa(x,y) = tileMovelEspecialFim Then mostrarChavesEscondidas = False
			Next
		Next

		; Se todas estiverem corretas
		If mostrarChavesEscondidas = True Then PlaySound chavesLiberada

	EndIf
End Function


; Verifica qual foi o ultimo level que o jogador chegou
; Se for true ele carrega o ultimo level, senao apenas recupera o valor da variavel levelSave
Function verificaLevelSave(atualizaEstado=False)
	file = ReadFile("arqs/save.ip") ; Abrindo arquivo para leitura
		temp_s$ = ReadLine(file)
		levelSave = temp_s$
		If (atualizaEstado And level < levelSave) Then level = levelSave
	CloseFile(file)
End Function

; Verifica se o level atual é o mais longe que o jogador alcançou, se for, salve
Function salvaUltimoMapa()
	If level > levelSave Then
		fileout = WriteFile ("arqs/save.ip")
		level_s$ = level
		WriteLine ( fileout, level_s$)
		levelSave = level
		CloseFile(fileout)
	EndIf
End Function

; Toca musica aleatória (se tiver musicas carregadas)
Function tocarMusica()
	If (Not ChannelPlaying(musica)) And numeroDeMusicas > 0 Then
		musica = PlaySound (musicas(Rnd(0,numeroDeMusicas-1)))
	EndIf
End Function

; Fazendo entrada !
Global fade_sprite = CreateSprite() ; cria sprite
EntityColor fade_sprite,0,0,0
MoveEntity fade_sprite,0,0,1 ; move para frente da camera

.inicio
EntityAlpha fade_sprite, 1
fadeInt# = 1

tocarMusica()

; Fazendo fade-in da tela inicial
FlushKeys()
While Not (KeyDown(teclaEnter) Or KeyDown(teclaL) Or KeyDown(ESC))
	Cls
	If fadeInt# > 0 Then fadeInt# = fadeInt# - 0.01
	EntityAlpha fade_sprite, fadeInt#
	DrawImage imgEntrada, 0, 0
	RenderWorld
	Flip()
Wend
verificaLevelSave(KeyDown(teclaL))

; Fazendo fade-out da tela inicial
While fadeInt# < 1
	Cls
	fadeInt# = fadeInt# + 0.01
	EntityAlpha fade_sprite, fadeInt#
	DrawImage imgEntrada, 0, 0
	RenderWorld
	Flip()
Wend
Delay 500

If level = 1 Then ; Se ele nao carregou outro mapa, mostra a historia
	; Fazendo entrada ! (parte2)
	FlushKeys()
	While Not GetKey()
		Cls
		DrawImage imgEntrada2,0,0
		Flip()
	Wend
EndIf

FlushKeys()


loadMap(level + ".ipm")

While Not KeyHit(1)
	Cls

	movendoJogador()
	tocarMusica()

	If movimentos > 5000 Then movimentos = 5000 ; evitando possiveis bugs (nao custa nada..)

	Select estado

		Case jogando
			printMap()
			printText()
			If addX#=0 And addY#=0 Then teclado() ; espera terminar de mover para o proximo tile

		Case fimDoLevel
			FlushKeys()
			; Aguarda jogador apertar uma tecla
			Cls
			printMap()
			printText()
			Flip()
			Delay 2200

			zeraVariaveis()
			level = level + 1
			If level > numeroDeLeveis Then
				estado = fimDoJogo
				level = 1
				Else
					loadMap(level + ".ipm")
					estado = iniciandoLevel
			EndIf

			salvaUltimoMapa()

		Case iniciandoLevel
			SetFont(Font1)
			DrawImage fundo, 0, 0
			level_introY#  = level_introY#  - 10
			Text 330, level_introY#, "LEVEL " + level

			If level_introY# <= -200 Then
				level_introY# = 600
				estado = iniciandoLevel2
			EndIf

			SetFont Font2


		Case iniciandoLevel2


			printMap(level_introY#, False)
			printText()
			If level_introY# <= 12 Then
				level_introY# = 700
				estado = jogando
				Else
					level_introY#  = level_introY#  - 12
			EndIf


		Case fimDoJogo
			img_fim = LoadImage("arqs/fim.png")
			snd_fim = LoadSound("arqs/fimDoJogo.wav")
			DrawImage img_fim, 0, 0
			PlaySound snd_fim
			Flip()
			WaitKey()
			estado = iniciandoLevel
			Goto inicio

	End Select


	Flip()
Wend

SetFont Font3
bg = LoadImage("arqs\bg1.png")
; ---- Final (créditos) ----
Dim SText$(999)				; Array com o texto que vai ser "rolado"
; Armazenando texto na array e contando numero de linhas do texto
linecount=0
file=ReadFile("arqs/creditos.cr")
While Not Eof(file)
	linecount=linecount+1
	SText$(linecount)=ReadLine$(file)
Wend

FlushKeys()
; Vamos fazer o looping do scroll :
y_final = (570 + FontHeight()*2*(linecount-12))
For text_y = -30 To y_final
	Cls
	DrawImage bg,0,0
	For temp=0 To linecount
		temp_t$ = SText$(temp)
		; Verificando se devemos aumentar a fonte (começa com %)
		temp_y# = ( 570-text_y + (StringHeight(temp_t$)*2*temp) )
		If Left$(temp_t$,1) = "%" Then
			SetFont font4
			temp_t$ = Right$(temp_t$, Len(temp_t$)-1) ; Retira o primeiro caracter
		EndIf
		; Tamanho é : numero de caracteres * largura dos caracteres
		tamanho# = StringWidth(temp_t$)
		Text 400-(tamanho#/2), temp_y#, temp_t$
		SetFont font3

		If text_y > -25 And KeyDown(1) Then Goto fim ; primeiro if é pra evitar bug.. =/

	Next
	Delay(2)
	Flip

Next
Text 5,550,"Aperte qualquer tecla para voltar ao Windows"
Flip()
FlushKeys()
WaitKey()

.fim
