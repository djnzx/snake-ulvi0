package com.codenjoy.dojo.snake.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.*;

import java.util.*;

/**
 * User: your name
 */
public class YourSolver implements Solver<Board> {

    private Dice dice;
    private Board board;
    int max = 0;

    public YourSolver(Dice dice) {
        this.dice = dice;
    }

    public boolean isApple(Point p){
        return board.getApples().contains(p);
    }

    public List<Point> getNeighbours(Point p){
        List<Point> points = new ArrayList<Point>(){{
                add(new PointImpl(p.getX(), p.getY() + 1));
                add(new PointImpl(p.getX(), p.getY() - 1));
                add( new PointImpl(p.getX() + 1, p.getY()));
                add(new PointImpl(p.getX() - 1, p.getY()));
            }};
        return points;
    }


    public Direction getDirection(Point a, Point b){
        if(b.getX() > a.getX()) return Direction.LEFT;
        if(b.getX() < a.getX()) return Direction.RIGHT;
        if(b.getY() > a.getY()) return Direction.DOWN;
        if(b.getY() < a.getY()) return Direction.UP;
        return Direction.STOP;
    }
    public Direction bfs(Point dst){
        Map<Point, Point> parent = new HashMap<>();
        Queue<Point> queue = new LinkedList<Point>();
        Set<Point> used = new HashSet<>();
        used.addAll(board.getBarriers());
        queue.add(board.getHead());
        used.remove(dst);
        while (!queue.isEmpty()){
            Point c = queue.remove();

            if(c.equals(dst)){
                while (!parent.get(c).equals(board.getHead())){
                    c = parent.get(c);
                }
                return getDirection(c, board.getHead());
            }

            for (Point p: getNeighbours(c)) {
                if(!used.contains(p)){
                    used.add(p);
                    parent.put(p, c);
                    queue.add(p);
                }
            }
        }
        return Direction.STOP;
    }



    @Override
    public String get(Board board) {
        this.board = board;
        if(max < board.getSnake().size()){
            max = board.getSnake().size();
        }
        if(board.isGameOver()){
            return Direction.random().toString();
        }
        Direction dir = bfs(board.getApples().get(0));
        if(dir == Direction.STOP){
            List<Point> list = board.getSnake();
            dir = bfs(list.get(list.size() - 1));
        }
        System.out.println(max);
        return dir.toString();
    }

    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                "http://104.248.35.51/codenjoy-contest/board/player/unn03f0r1eu2pniy1e6y?code=1459577551075674256",
                new YourSolver(new RandomDice()),
                new Board());
    }

}
