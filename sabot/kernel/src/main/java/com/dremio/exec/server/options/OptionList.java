/*
 * Copyright (C) 2017-2018 Dremio Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dremio.exec.server.options;

import static com.dremio.exec.server.options.OptionValue.OptionType.SYSTEM;

import java.util.ArrayList;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

public class OptionList extends ArrayList<OptionValue>{

  public void merge(OptionList list){
    this.addAll(list);
  }

  private final static Function<OptionValue, String> EXTRACT_OPTION_NAME = new Function<OptionValue, String>() {
    @Override
    public String apply(OptionValue input) {
      return input.name;
    }
  };
  private final static Predicate<OptionValue> IS_SYSTEM_OPTION = new Predicate<OptionValue>() {
    @Override
    public boolean apply(OptionValue option) {
      return option.type == SYSTEM;
    }
  };

  /**
   * only merge options from list that are not already set locally
   */
  public void mergeIfNotPresent(OptionList list) {
    Set<String> options = Sets.newHashSet(Iterables.transform(this, EXTRACT_OPTION_NAME));
    for (OptionValue optionValue : list) {
      if (!options.contains(optionValue.name)) {
        this.add(optionValue);
      }
    }
  }

  public OptionList getSystemOptions() {
    final OptionList options = new OptionList();
    Iterators.addAll(options, Iterators.filter(iterator(), IS_SYSTEM_OPTION));
    return options;
  }

  public OptionList getNonSystemOptions() {
    final OptionList options = new OptionList();
    Iterators.addAll(options, Iterators.filter(iterator(), Predicates.not(IS_SYSTEM_OPTION)));
    return options;
  }
}
