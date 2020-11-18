function startGame() {

    $.ajax( {
        url: 'http://localhost:8080/api/begin/0/0',
        method: 'POST',
        success: function( battleshipGame ) {
            resetBoard();
            $("#playerId").text("Player: " + battleshipGame.player1.playerId);
        }
    } );
}

//function playGame(battleshipGame) {
//    if (battleshipGame.playerTurn == 0) {
//        setupShips(battleshipGame.player1.boardId);
//        setupShips(battleshipGame.player2.boardId);
//        updateTurn(battleshipGame.gameId, 1);
//    }
//
//}
//
//function buildBoard(gameId, playerNumber) {
//    $.ajax( {
//        url: 'http://localhost:8080/api/playerboard/' + gameId + '/' + playerNumber,
//        method: 'GET',
//        success: function( playerBoard ) {
//
//            }
//        } );
//}
//
//function updateTurn(gameId, newTurn) {
//    $.ajax( {
//        url: 'http://localhost:8080/api/updateTurn/' + gameId + '/' + newTurn,
//        method: 'POST',
//        success: function( ) {
//            $("#playerId").text("Player: " + newTurn);
//        }
//    } );
//}
//
//function setupShips(boardId) {
//        let placeShip = {};
//        //Build Carrier
//        guessObject.gameId = $('#gameId').val();
//        guessObject.guess = $('#chosenLetter').val();
//
//
//}
//
//function placeShips(placeShip) {
//    $.ajax( {
//                method: 'PUT',
//                url: 'http://localhost:8080/api/placeship',
//                contentType: "application/json",
//                data: JSON.stringify(placeShip)
//                success: function ( board ) {
//                    loadBoard( board );
//                }
//            });
//}

function resetBoard() {
//Reset all board squares to empty
    $('#00').attr("class", "empty");
    $('#01').attr("class", "empty");
    $('#02').attr("class", "empty");
    $('#03').attr("class", "empty");
    $('#04').attr("class", "empty");
    $('#05').attr("class", "empty");
    $('#06').attr("class", "empty");
    $('#07').attr("class", "empty");
    $('#08').attr("class", "empty");
    $('#09').attr("class", "empty");
    $('#10').attr("class", "empty");
    $('#11').attr("class", "empty");
    $('#12').attr("class", "empty");
    $('#13').attr("class", "empty");
    $('#14').attr("class", "empty");
    $('#15').attr("class", "empty");
    $('#16').attr("class", "empty");
    $('#17').attr("class", "empty");
    $('#18').attr("class", "empty");
    $('#19').attr("class", "empty");
    $('#20').attr("class", "empty");
    $('#21').attr("class", "empty");
    $('#22').attr("class", "empty");
    $('#23').attr("class", "empty");
    $('#24').attr("class", "empty");
    $('#25').attr("class", "empty");
    $('#26').attr("class", "empty");
    $('#27').attr("class", "empty");
    $('#28').attr("class", "empty");
    $('#29').attr("class", "empty");
    $('#30').attr("class", "empty");
    $('#31').attr("class", "empty");
    $('#32').attr("class", "empty");
    $('#33').attr("class", "empty");
    $('#34').attr("class", "empty");
    $('#35').attr("class", "empty");
    $('#36').attr("class", "empty");
    $('#37').attr("class", "empty");
    $('#38').attr("class", "empty");
    $('#39').attr("class", "empty");
    $('#40').attr("class", "empty");
    $('#41').attr("class", "empty");
    $('#42').attr("class", "empty");
    $('#43').attr("class", "empty");
    $('#44').attr("class", "empty");
    $('#45').attr("class", "empty");
    $('#46').attr("class", "empty");
    $('#47').attr("class", "empty");
    $('#48').attr("class", "empty");
    $('#49').attr("class", "empty");
    $('#50').attr("class", "empty");
    $('#51').attr("class", "empty");
    $('#52').attr("class", "empty");
    $('#53').attr("class", "empty");
    $('#54').attr("class", "empty");
    $('#55').attr("class", "empty");
    $('#56').attr("class", "empty");
    $('#57').attr("class", "empty");
    $('#58').attr("class", "empty");
    $('#59').attr("class", "empty");
    $('#60').attr("class", "empty");
    $('#61').attr("class", "empty");
    $('#62').attr("class", "empty");
    $('#63').attr("class", "empty");
    $('#64').attr("class", "empty");
    $('#65').attr("class", "empty");
    $('#66').attr("class", "empty");
    $('#67').attr("class", "empty");
    $('#68').attr("class", "empty");
    $('#69').attr("class", "empty");
    $('#70').attr("class", "empty");
    $('#71').attr("class", "empty");
    $('#72').attr("class", "empty");
    $('#73').attr("class", "empty");
    $('#74').attr("class", "empty");
    $('#75').attr("class", "empty");
    $('#76').attr("class", "empty");
    $('#77').attr("class", "empty");
    $('#78').attr("class", "empty");
    $('#79').attr("class", "empty");
    $('#80').attr("class", "empty");
    $('#81').attr("class", "empty");
    $('#82').attr("class", "empty");
    $('#83').attr("class", "empty");
    $('#84').attr("class", "empty");
    $('#85').attr("class", "empty");
    $('#86').attr("class", "empty");
    $('#87').attr("class", "empty");
    $('#88').attr("class", "empty");
    $('#89').attr("class", "empty");
    $('#90').attr("class", "empty");
    $('#91').attr("class", "empty");
    $('#92').attr("class", "empty");
    $('#93').attr("class", "empty");
    $('#94').attr("class", "empty");
    $('#95').attr("class", "empty");
    $('#96').attr("class", "empty");
    $('#97').attr("class", "empty");
    $('#98').attr("class", "empty");
    $('#99').attr("class", "empty");
}

//function loadGame( battleshipGame ) {
//$.ajax( {
//    url: 'http://localhost:8080/api/gamestate/' + battleshipGame.gameId,
//    method: 'GET',
//    success: function( data ) {
//            $( '#gameId' ).val( data.gameId );
//            $( '#player1board' ).text( data.player1.boardId );
//            $( '#player2board' ).text( data.player2.boardId );
//            $( '#playerTurn' ).text( data.playerTurn);
//        }
//    } );
//}

//function loadBoard() {
//$.ajax( {
//    url: 'http://localhost:8080/api/gamestate/' + id,
//    method: 'GET',
//    success: function( data ) {
//    //        console.log( data );
//    //        raw JS
//    //        let uncoveredParagraph = document.getElementById( 'uncovered' );
//    //        uncovered.innerHTML = data.partial;
//
//    //        jQuery
//            $( '#gameId' ).val( data.gameId );
//            $( '#uncovered' ).text( data.partial );
//
//            let letterString  = '';
//            for ( let i = 0; i < data.guessedLetters.length; i++){
//                letterString += data.guessedLetters[i] + ' ';
//            }
//            $( '#selectedLetters' ).text( letterString );
//            $( '#movesRemaining' ).text( 'Remaining moves: ' + data.movesRemaining );
//        }
//    error:
//    } );
//}
//
//function submitGuess() {
//
//    let guessObject = {};
//    guessObject.gameId = $('#gameId').val();
//    guessObject.guess = $('#chosenLetter').val();
//
//    $.ajax( {
//        method: 'PUT',
//        url: 'http://localhost:8080/api/guessletter',
//        contentType: "application/json",
//        data: JSON.stringify(guessObject)
//        success: function ( board ) {
//            loadBoard( board );
//        }
//    });
//}
