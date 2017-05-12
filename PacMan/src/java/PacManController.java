/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Kesara
 */
@WebServlet(urlPatterns = {"/pacman","/update"})
public class PacManController extends HttpServlet {

    private PacManGame game = new PacManGame(45, 45);
    private PacManPlayer player;

    @Override
    public void init() {
//        game.start();
        Logger.getGlobal().log(Level.INFO, "Game Started");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/event-stream, charset=utf-8");
        response.flushBuffer();
        Logger.getGlobal().log(Level.INFO, "Beginning update stream.");

        try (PrintWriter out = response.getWriter()) {
            

            out.print("data: ");
            out.println(game.getBoardState());
            
            out.println();
            out.flush();
            
            while (!Thread.interrupted())
                synchronized (this) {
                    wait();

                    out.print("data: ");
                    out.println(game.getBoardState());
                    out.println();
                    out.flush();
//                    System.out.println(game.getBoardJSON());
//                    Logger.getGlobal().log(Level.INFO, game.getBoardJSON());
                }
        } catch (InterruptedException e) {
            Logger.getGlobal().log(Level.INFO, "Terminating updates.");
            response.setStatus(HttpServletResponse.SC_GONE);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if(request.getServletPath().equals("/update")){
            int key = Integer.parseInt(request.getParameter("keypress"));
            
            synchronized(this){
                switch (key) {
                    case 37:
                        game.keyPress(player, 'L');
                        break;
                    case 38:
                        game.keyPress(player, 'U');
                        break;
                    case 39:
                        game.keyPress(player, 'R');
                        break;
                    case 40:
                        game.keyPress(player, 'D');
                        break;
                    default:
                        break;
                }
                notifyAll();
            }
        }
    }

}
