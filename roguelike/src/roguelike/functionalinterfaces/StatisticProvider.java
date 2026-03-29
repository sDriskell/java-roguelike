package roguelike.functionalinterfaces;

import java.io.Serializable;

import roguelike.actors.Statistics;
import roguelike.actors.Statistics.Statistic;

/**
 * 
 */
public interface StatisticProvider extends Serializable {

  /**
   * 
   * @param argStat
   * @return
   */
  public Statistic get(Statistics argStat);
}
