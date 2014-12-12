/* 
 * Copyright (C) 2014 William Shere
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.quew8.ttg.parser;

import java.util.ArrayList;

/**
 *
 * @author Quew8
 */
public class MatchResult {
    private int pos;
    private final TokenSequence sequence;
    private final ArrayList<GroupResult> groupMatches = new ArrayList<GroupResult>();
    
    public MatchResult(TokenSequence sequence, int pos) {
        this.sequence = sequence;
        this.pos = pos;
    }
    
    public MatchResult(TokenSequence sequence) {
        this(sequence, 0);
    }
    
    protected void addGroupResult(int offset, int n) {
        groupMatches.add(new GroupResult(offset, n));
    }
    
    private GroupResult getGroupResult(int group) {
        return groupMatches.get(groupMatches.size() - 1 - group);
    }
    
    public Token getNextToken() {
        return sequence.get(pos);
    }
    
    public int getStart(int group) {
        return getGroupResult(group).offset;
    }
    
    public int getLength(int group) {
        return getGroupResult(group).n;
    }
    
    public void putGroup(int group, Token[] dest, int offset) {
        getGroupResult(group).putIn(dest, offset);
    }
    
    public Token[] getGroup(int group) {
        Token[] groupSequence = new Token[getLength(group)];
        getGroupResult(group).putIn(groupSequence, 0);
        return groupSequence;
    }
    
    public int getNGroups() {
        return groupMatches.size();
    }
    
    public Token matchToken(TokenType tt) {
        if(match(tt)) {
            return sequence.get(pos - 1);
        } else {
            return null;
        }
    }
    
    public ComplexToken matchComplexToken(TokenSequenceMatcher matcher) {
        int i = matchLength(matcher);
        if(i == -1) {
            return null;
        } else {
            Token[] subSeq = new Token[i];
            sequence.putIn(pos - i, subSeq, 0, i);
            return new ComplexToken(subSeq);
        }
    }
    
    public boolean match(TokenSequenceMatcher matcher) {
        return matchLength(matcher) != -1;
    }
    
    public int matchLength(TokenSequenceMatcher matcher) {
        int i = matcher.match(this, sequence, pos);
        if(i == -1) {
            return -1;
        }
        pos += i;
        return i;
    }
    
    public Snapshot takeSnapshot() {
        return new Snapshot(pos, groupMatches.size());
    }
    
    public void revert(Snapshot snapshot) {
        this.pos = snapshot.pos;
        for(int i = snapshot.n; i < groupMatches.size(); i++) {
            groupMatches.remove(i);
        }
    }
    
    private class GroupResult {
        private final int offset;
        private final int n;
        
        private GroupResult(int offset, int n) {
            this.offset = offset;
            this.n = n;
        }
        
        public void putIn(Token[] dest, int dstOffset) {
            sequence.putIn(offset, dest, dstOffset, n);
        }
    }
    
    public class Snapshot {
        private final int pos;
        private final int n;

        public Snapshot(int pos, int n) {
            this.pos = pos;
            this.n = n;
        }
    }
}
